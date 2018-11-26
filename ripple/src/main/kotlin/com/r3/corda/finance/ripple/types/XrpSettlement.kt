package com.r3.corda.finance.ripple.types

import com.r3.corda.finance.obligation.types.DigitalCurrency
import com.r3.corda.finance.obligation.types.OffLedgerPayment
import com.r3.corda.finance.ripple.flows.MakeXrpPayment
import com.ripple.core.coretypes.AccountID
import net.corda.core.contracts.Amount
import net.corda.core.identity.Party

/**
 * Terms specific to settling with XRP. In this case, parties must agree on:
 * - which ripple address the payment must be made to
 * - which servers should be used to check the payment was successful
 *
 * The terms can be updated with:
 * - the hash of the ripple transaction when the ripple payment is submitted
 * - a payment settlementStatus
 */
data class XrpSettlement(
        override val accountToPay: AccountID,
        override val settlementOracle: Party,
        override val paymentFlow: Class<MakeXrpPayment<*>> = MakeXrpPayment::class.java
) : OffLedgerPayment<MakeXrpPayment<*>>