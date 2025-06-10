package com.example.labelly_application.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Utilizatorul curent
    val currentUser: FirebaseUser? = auth.currentUser

    // Login cu email și parolă
    suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            if (result.user != null) {
                Result.success(result.user!!)
            } else {
                Result.failure(Exception("Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Înregistrare cu email și parolă
    suspend fun register(email: String, password: String, username: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user

            if (user != null) {
                // Salvează DOAR informațiile publice ale utilizatorului în Firestore
                // ATENȚIE: Nu salvăm niciodată parola în baza de date!
                val userMap = mapOf(
                    "uid" to user.uid,
                    "email" to email,
                    "username" to username,
                    "createdAt" to System.currentTimeMillis(),
                    "profileCompleted" to false
                )

                firestore.collection("users")
                    .document(user.uid)
                    .set(userMap)
                    .await()

                Result.success(user)
            } else {
                Result.failure(Exception("Registration failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Resetare parolă
    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Schimbarea parolei pentru utilizatorul logat
    suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit> {
        return try {
            val user = currentUser ?: return Result.failure(Exception("User not logged in"))

            // Re-autentifică utilizatorul cu parola curentă
            val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(user.email!!, currentPassword)
            user.reauthenticate(credential).await()

            // Schimbă parola
            user.updatePassword(newPassword).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Logout
    fun logout() {
        auth.signOut()
    }

    // Verifică dacă utilizatorul este logat
    fun isUserLoggedIn(): Boolean {
        return currentUser != null
    }

    // Obține informațiile utilizatorului din Firestore
    suspend fun getUserInfo(uid: String? = null): Result<Map<String, Any>> {
        return try {
            val userId = uid ?: currentUser?.uid ?: return Result.failure(Exception("User not logged in"))

            val document = firestore.collection("users")
                .document(userId)
                .get()
                .await()

            if (document.exists()) {
                Result.success(document.data ?: emptyMap())
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Actualizează informațiile utilizatorului
    suspend fun updateUserInfo(updates: Map<String, Any>): Result<Unit> {
        return try {
            val userId = currentUser?.uid ?: return Result.failure(Exception("User not logged in"))

            firestore.collection("users")
                .document(userId)
                .update(updates)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}