package com.example.eloanmust.feature.loan.domain.usecase

import com.example.eloanmust.core.common.Resource
import com.example.eloanmust.feature.loan.domain.model.Loan
import com.example.eloanmust.feature.loan.domain.model.LoanApplication
import com.example.eloanmust.feature.loan.domain.repository.LoanRepository
import javax.inject.Inject

/**
 * Use case for applying for a loan.
 */
class ApplyLoanUseCase @Inject constructor(
    private val loanRepository: LoanRepository
) {
    /**
     * Execute loan application
     * @param application Loan application data
     * @return Resource containing created Loan
     */
    suspend operator fun invoke(application: LoanApplication): Resource<Loan> {
        // Validate inputs
        if (application.amount <= 0) {
            return Resource.Error("Nominal pinjaman harus lebih dari 0")
        }
        
        if (application.amount < 1_000_000) {
            return Resource.Error("Minimal pinjaman Rp 1.000.000")
        }
        
        if (application.tenor <= 0) {
            return Resource.Error("Tenor harus lebih dari 0 bulan")
        }
        
        return loanRepository.applyLoan(application)
    }
}
