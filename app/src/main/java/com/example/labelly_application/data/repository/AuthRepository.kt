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
                // Salvează informațiile utilizatorului în Firestore
                val userMap = mapOf(
                    "uid" to user.uid,
                    "email" to email,
                    "username" to username,
                    "createdAt" to System.currentTimeMillis()
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

    // Logout
    fun logout() {
        auth.signOut()
    }

    // Verifică dacă utilizatorul este logat
    fun isUserLoggedIn(): Boolean {
        return currentUser != null
    }

    // Obține informațiile utilizatorului din Firestore
    suspend fun getUserInfo(uid: String): Result<Map<String, Any>> {
        return try {
            val document = firestore.collection("users")
                .document(uid)
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
}