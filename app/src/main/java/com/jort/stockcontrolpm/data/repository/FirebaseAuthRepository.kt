package com.jort.stockcontrolpm.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

/**
 * FirebaseAuthRepository
 *
 * Encapsula todas las operaciones de autenticación con Firebase Authentication.
 * Sigue el patrón Repository: la UI nunca toca FirebaseAuth directamente,
 * siempre pasa por aquí. Esto facilita pruebas y cambios futuros.
 *
 * Resultado sellado: cada función devuelve AuthResult para que el ViewModel
 * sepa si fue éxito o qué tipo de error ocurrió, sin necesidad de try/catch
 * en la capa de UI.
 */
class FirebaseAuthRepository {

    // Instancia singleton de FirebaseAuth — punto de entrada al SDK de Auth
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // ─────────────────────────────────────────────────────────────────────────
    // Tipo sellado: modela los posibles resultados de una operación de Auth
    // ─────────────────────────────────────────────────────────────────────────
    sealed class AuthResult {
        /** Operación exitosa. Contiene el usuario autenticado. */
        data class Success(val user: FirebaseUser) : AuthResult()

        /** Error conocido con mensaje legible para mostrar al usuario. */
        data class Error(val message: String) : AuthResult()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // REGISTRO
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Crea una nueva cuenta en Firebase con email y contraseña.
     *
     * Firebase valida el formato del email y la fortaleza de la contraseña
     * automáticamente. Si el email ya existe, lanza CollisionException.
     *
     * @param email    Correo del nuevo usuario
     * @param password Contraseña (mínimo 6 caracteres por política de Firebase)
     * @return AuthResult.Success con el usuario creado, o AuthResult.Error con mensaje
     */
    suspend fun register(email: String, password: String): AuthResult {
        return try {
            // createUserWithEmailAndPassword: llama al servidor de Firebase,
            // crea la cuenta y devuelve un AuthResult con el FirebaseUser.
            // .await() convierte la Task<AuthResult> de Java a una corrutina de Kotlin.
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: return AuthResult.Error("No se pudo crear el usuario")
            AuthResult.Success(user)

        } catch (e: FirebaseAuthWeakPasswordException) {
            // La contraseña tiene menos de 6 caracteres
            AuthResult.Error("La contraseña debe tener al menos 6 caracteres")

        } catch (e: FirebaseAuthUserCollisionException) {
            // El email ya está registrado en este proyecto Firebase
            AuthResult.Error("Este correo ya tiene una cuenta registrada")

        } catch (e: FirebaseAuthInvalidCredentialsException) {
            // Formato de email inválido (ej: "usuario@" sin dominio)
            AuthResult.Error("El formato del correo no es válido")

        } catch (e: Exception) {
            // Cualquier otro error: sin conexión, timeout, etc.
            AuthResult.Error(e.message ?: "Error al registrar. Revisa tu conexión.")
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // INICIO DE SESIÓN
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Inicia sesión con email y contraseña existentes.
     *
     * Firebase devuelve un token de sesión que se guarda automáticamente
     * en el dispositivo. La próxima vez que abras la app, [getCurrentUser]
     * devolverá este usuario sin necesidad de hacer login de nuevo.
     *
     * @param email    Correo registrado
     * @param password Contraseña del usuario
     * @return AuthResult.Success con el usuario, o AuthResult.Error con mensaje
     */
    suspend fun login(email: String, password: String): AuthResult {
        return try {
            // signInWithEmailAndPassword: verifica las credenciales contra Firebase,
            // y si son correctas persiste la sesión localmente.
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: return AuthResult.Error("No se pudo iniciar sesión")
            AuthResult.Success(user)

        } catch (e: FirebaseAuthInvalidUserException) {
            // El email no existe en este proyecto de Firebase
            AuthResult.Error("No existe una cuenta con ese correo")

        } catch (e: FirebaseAuthInvalidCredentialsException) {
            // Contraseña incorrecta o email mal formateado
            AuthResult.Error("Correo o contraseña incorrectos")

        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Error al iniciar sesión. Revisa tu conexión.")
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SESIÓN ACTIVA
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Devuelve el usuario actualmente autenticado, o null si no hay sesión.
     *
     * Firebase guarda el token de sesión en el dispositivo. Esta función
     * es síncrona (no necesita red): lee el estado local del SDK.
     * Se usa en MainActivity para decidir si ir al Login o al Dashboard.
     */
    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    /**
     * true si hay un usuario con sesión activa, false si no.
     * Atajo conveniente para usar en condiciones de navegación.
     */
    fun isLoggedIn(): Boolean = auth.currentUser != null

    /**
     * Devuelve el UID del usuario autenticado, o cadena vacía si no hay sesión.
     *
     * El UID es el identificador único que Firebase asigna a cada cuenta.
     * Se usa como clave en Firestore: usuarios/{uid}/productos/...
     * De esta forma cada usuario solo puede ver y modificar sus propios datos.
     */
    fun getCurrentUid(): String = auth.currentUser?.uid ?: ""

    /**
     * Devuelve el nombre que el usuario registró (displayName de Firebase).
     * Si no tiene nombre guardado, devuelve el email como fallback.
     */
    fun getDisplayName(): String =
        auth.currentUser?.displayName?.takeIf { it.isNotBlank() }
            ?: auth.currentUser?.email
            ?: "Usuario"

    /**
     * Guarda el nombre del usuario en el perfil de Firebase Auth.
     * Se llama justo después de createUserWithEmailAndPassword().
     */
    suspend fun updateDisplayName(name: String) {
        val request = com.google.firebase.auth.UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .build()
        auth.currentUser?.updateProfile(request)?.await()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CIERRE DE SESIÓN
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Cierra la sesión actual.
     *
     * Elimina el token guardado en el dispositivo. La próxima vez que se abra
     * la app, [getCurrentUser] devolverá null y el usuario verá el Login.
     * Esta función es síncrona (no necesita red).
     */
    fun logout() {
        auth.signOut()
    }
}
