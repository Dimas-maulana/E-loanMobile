package com.example.eloanmust.feature.loan.domain.usecase

import com.example.eloanmust.core.common.Resource
import com.example.eloanmust.feature.loan.domain.model.LoanSimulation
import com.example.eloanmust.feature.loan.domain.repository.LoanRepository
import javax.inject.Inject

/**
 * Use case for loan simulation.
 */
class SimulateLoanUseCase @Inject constructor(
    private val loanRepository: LoanRepository
) {
    /**
     * Execute loan simulation
     * @param amount Loan amount
     * @param tenor Loan tenor in months
     * @return Resource containing LoanSimulation
     */
    suspend operator fun invoke(amount: Double, tenor: Int): Resource<LoanSimulation> {
        // Validate inputs
        if (amount <= 0) {
            return Resource.Error("Nominal pinjaman harus lebih dari 0")
        }

        if (amount < 1_000_000) {
            return Resource.Error("Minimal pinjaman Rp 1.000.000")
        }

        if (amount > 500_000_000) {
            return Resource.Error("Maksimal pinjaman Rp 500.000.000")
        }

        if (tenor <= 0) {
            return Resource.Error("Tenor harus lebih dari 0 bulan")
        }

        if (tenor > 60) {
            return Resource.Error("Maksimal tenor 60 bulan")
        }

        return loanRepository.simulateLoan(amount, tenor)
    }
}
