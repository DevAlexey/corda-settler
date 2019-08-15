package com.r3.corda.finance.swift.services

import com.r3.corda.finance.swift.types.SWIFTPaymentResponse
import com.r3.corda.finance.swift.types.SWIFTPaymentStatus
import com.r3.corda.finance.swift.types.SWIFTPaymentStatusType
import com.r3.corda.finance.swift.types.SWIFTTransactionStatus
import com.r3.corda.lib.tokens.contracts.types.TokenType
import net.corda.core.contracts.Amount
import org.slf4j.LoggerFactory
import java.security.PrivateKey
import java.security.cert.X509Certificate
import java.util.*

class SWIFTClientMock(
    private val apiUrl: String,
    private val apiKey: String,
    private val privateKey: PrivateKey,
    private val certificate: X509Certificate
) : SWIFTClient(apiUrl, apiKey, privateKey, certificate) {
    companion object {
        private val logger = LoggerFactory.getLogger(SWIFTClientMock::class.java)!!
        private val transferStatus = mutableMapOf<String, SWIFTTransactionStatus>()
    }

    /**
     * Submits a payment to the SWIFT gateway
     */
    override fun makePayment(
        e2eId: String,
        executionDate: Date,
        amount: Amount<TokenType>,
        debtorName: String,
        debtorLei: String,
        debtorIban: String,
        debtorBicfi: String,
        creditorName: String,
        creditorLei: String,
        creditorIban: String,
        creditorBicfi: String,
        remittanceInformation: String
    ): SWIFTPaymentResponse {

        val payment = SWIFTPaymentResponse(null, UUID.randomUUID().toString())

        transferStatus[payment.uetr] = SWIFTTransactionStatus(SWIFTPaymentStatusType.ACSP)

        return payment
    }

    /**
     * Fetches SWIFT payment status
     */
    override fun getPaymentStatus(uetr: String): SWIFTPaymentStatus {
        val result = transferStatus[uetr]
        if (result == null) {
            val message = "Transfer with UETR($uetr) not found"
            logger.warn(message)
            throw SWIFTPaymentException(message)
        } else {
            logger.info("Successfully retrieved payment status with UETR($uetr)")
            return SWIFTPaymentStatus(uetr, result)
        }
    }

    /**
     * TODO: This method should be eventually removed. This API is open for testing only.
     */
    override fun updatePaymentStatus(uetr: String, status: SWIFTPaymentStatusType) {
        if (!transferStatus.containsKey(uetr)) {
            val message = "Transfer with UETR($uetr) not found for update"
            logger.warn(message)
            throw SWIFTPaymentException(message)
        } else {
            transferStatus[uetr] = SWIFTTransactionStatus(status)
            logger.info("Successfully updated payment status with UETR($uetr)")
        }
    }
}
