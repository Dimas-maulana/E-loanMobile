package com.example.eloanmust.feature.loan.domain.usecase

import com.example.eloanmust.core.common.Resource
import com.example.eloanmust.feature.loan.domain.model.Loan
import com.example.eloanmust.feature.loan.domain.repository.LoanRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting user's loans (offline-first).
 */
class GetMyLoansUseCase @Inject constructor(
    private val loanRepository: LoanRepository
) {
    /**
     * Execute get my loans
     * @return Flow of Resource containing list of loans
     */
    operator fun invoke(): Flow<Resource<List<Loan>>> {
        return loanRepository.getMyLoans()
    }
}
