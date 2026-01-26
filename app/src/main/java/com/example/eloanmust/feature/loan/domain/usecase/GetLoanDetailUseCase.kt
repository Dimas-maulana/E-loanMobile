package com.example.eloanmust.feature.loan.domain.usecase

import com.example.eloanmust.core.common.Resource
import com.example.eloanmust.feature.loan.domain.model.Loan
import com.example.eloanmust.feature.loan.domain.repository.LoanRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting loan detail by ID.
 */
class GetLoanDetailUseCase @Inject constructor(
    private val loanRepository: LoanRepository
) {
    /**
     * Execute get loan detail
     * @param loanId Loan ID
     * @return Flow of Resource containing Loan
     */
    operator fun invoke(loanId: Long): Flow<Resource<Loan>> {
        return loanRepository.getLoanById(loanId)
    }
}
