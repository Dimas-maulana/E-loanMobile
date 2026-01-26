package com.example.eloanmust.core.di

import com.example.eloanmust.feature.auth.data.repository.AuthRepositoryImpl
import com.example.eloanmust.feature.auth.domain.repository.AuthRepository
import com.example.eloanmust.feature.loan.data.repository.LoanRepositoryImpl
import com.example.eloanmust.feature.loan.domain.repository.LoanRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for binding repository interfaces to implementations.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    /**
     * Binds AuthRepository implementation
     */
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository
    
    /**
     * Binds LoanRepository implementation
     */
    @Binds
    @Singleton
    abstract fun bindLoanRepository(
        loanRepositoryImpl: LoanRepositoryImpl
    ): LoanRepository
}
