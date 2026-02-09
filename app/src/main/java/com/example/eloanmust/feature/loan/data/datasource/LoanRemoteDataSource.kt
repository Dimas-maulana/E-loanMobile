package com.example.eloanmust.feature.loan.data.datasource

import com.example.eloanmust.core.network.ApiResponse
import com.example.eloanmust.core.network.ApiService
import com.example.eloanmust.feature.loan.data.dto.LoanApplicationRequest
import com.example.eloanmust.feature.loan.data.dto.LoanDto
import com.example.eloanmust.feature.loan.data.dto.LoanSimulationRequest
import com.example.eloanmust.feature.loan.data.dto.LoanSimulationResponse
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Remote data source for loan operations.
 */
@Singleton
class LoanRemoteDataSource @Inject constructor(
    private val apiService: ApiService
) {
    /**
     * Simulate loan via API
     */
    suspend fun simulateLoan(request: LoanSimulationRequest): Response<ApiResponse<LoanSimulationResponse>> {
        return apiService.simulateLoan(request)
    }

    /**
     * Apply for loan via API
     */
    suspend fun applyLoan(request: LoanApplicationRequest): Response<ApiResponse<LoanDto>> {
        return apiService.applyLoan(request)
    }

    /**
     * Get user's loans via API
     */
    suspend fun getMyLoans(): Response<ApiResponse<List<LoanDto>>> {
        return apiService.getMyLoans()
    }

    /**
     * Get loan by ID via API
     */
    suspend fun getLoanById(loanId: Long): Response<ApiResponse<LoanDto>> {
        return apiService.getLoanById(loanId)
    }
}
