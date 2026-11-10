package com.bca.mobile.data.repository

import com.bca.mobile.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class BcaRepository private constructor() {

    private val _account = MutableStateFlow(
        Account(
            accountNumber = "0060387737",
            accountType = "TABUNGAN TAHAPAN",
            accountHolder = "DADANG MUBAROK",
            balance = 261232130240.16,
            currency = "IDR",
            cardNumber = "5307 9521 1830 4782",
            cardType = "Paspor BCA Mastercard Platinum"
        )
    )
    val account: StateFlow<Account> = _account.asStateFlow()

    private val _networkSignal = MutableStateFlow(NetworkSignalState.READY)
    val networkSignal: StateFlow<NetworkSignalState> = _networkSignal.asStateFlow()

    // Authentic BCA Connection Indicator: biru saat baru masuk menu 1.5 detik, terus hijau terus (tidak looping)
    private val _indicatorSignal = MutableStateFlow(NetworkSignalState.PROCESSING)
    val indicatorSignal: StateFlow<NetworkSignalState> = _indicatorSignal.asStateFlow()

    private val repositoryScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var transitionJob: Job? = null

    init {
        // Awal masuk aplikasi: biru 1.5 detik, lalu hijau terus
        triggerMenuTransition(1500L)
    }

    fun triggerMenuTransition(durationMs: Long = 1500L) {
        if (_networkSignal.value == NetworkSignalState.OFFLINE) {
            _indicatorSignal.value = NetworkSignalState.OFFLINE
            return
        }
        transitionJob?.cancel()
        _indicatorSignal.value = NetworkSignalState.PROCESSING
        transitionJob = repositoryScope.launch {
            delay(durationMs)
            if (_networkSignal.value != NetworkSignalState.OFFLINE) {
                _indicatorSignal.value = NetworkSignalState.READY
            }
        }
    }

    fun setNetworkSignal(state: NetworkSignalState) {
        _networkSignal.value = state
        if (state == NetworkSignalState.OFFLINE) {
            _indicatorSignal.value = NetworkSignalState.OFFLINE
        }
    }

    fun triggerProcessingPulse(durationMs: Long = 1500L) {
        triggerMenuTransition(durationMs)
    }

    suspend fun <T> runWithNetworkSimulation(block: suspend () -> T): T {
        if (_networkSignal.value == NetworkSignalState.OFFLINE) {
            throw IllegalStateException("205 - Silakan tunggu beberapa saat sampai lampu indikator di pojok kanan atas berwarna hijau sebelum melanjutkan transaksi.")
        }
        val prevState = _networkSignal.value
        _networkSignal.value = NetworkSignalState.PROCESSING
        return try {
            kotlinx.coroutines.delay(900)
            block()
        } finally {
            _networkSignal.value = prevState
        }
    }

    private val _mutations = MutableStateFlow(
        listOf(
            Transaction(
                id = "TRX-001",
                date = "25/10 10:15",
                description = "TRSF E-BANKING CR DARI BUDI SANTOSO",
                amount = 2_500_000L,
                type = TransactionType.CR,
                balanceAfter = 25_850_000L
            ),
            Transaction(
                id = "TRX-002",
                date = "24/10 19:40",
                description = "DEBIT EDC RESTO SEDAP",
                amount = 320_000L,
                type = TransactionType.DB,
                balanceAfter = 23_350_000L
            ),
            Transaction(
                id = "TRX-003",
                date = "23/10 14:22",
                description = "TARIK TUNAI ATM BCA PIM",
                amount = 1_000_000L,
                type = TransactionType.DB,
                balanceAfter = 23_670_000L
            ),
            Transaction(
                id = "TRX-004",
                date = "22/10 09:05",
                description = "TRSF E-BANKING CR GAJI PT RAPTECH DIGITAL",
                amount = 12_500_000L,
                type = TransactionType.CR,
                balanceAfter = 24_670_000L
            ),
            Transaction(
                id = "TRX-005",
                date = "20/10 16:30",
                description = "BCA VIRTUAL ACCOUNT TOKOPEDIA",
                amount = 450_000L,
                type = TransactionType.DB,
                balanceAfter = 12_170_000L
            )
        )
    )
    val mutations: StateFlow<List<Transaction>> = _mutations.asStateFlow()

    private val _recipients = MutableStateFlow(
        listOf(
            TransferRecipient("REC-FIQIH", "3741720467", "FIQIH NURAJIZAH", "BCA"),
            TransferRecipient("REC-1", "2181475538", "ABDILLAH", "BCA"),
            TransferRecipient("REC-2", "2481574694", "ACHMAD RIZAL", "BCA"),
            TransferRecipient("REC-3", "6520643140", "ADE FERY PERMANA", "BCA"),
            TransferRecipient("REC-4", "2990889042", "ADE JAJANG NURJAMAN", "BCA"),
            TransferRecipient("REC-5", "0881234567", "BUDI SANTOSO", "BCA"),
            TransferRecipient("REC-6", "1400012345678", "HENDRA WIJAYA", "BANK MANDIRI"),
            TransferRecipient("REC-7", "001201998877501", "DEWI LESTARI", "BRI"),
            TransferRecipient("REC-8", "0779876543", "SITI RAHMAWATI", "BNI"),
            TransferRecipient("REC-9", "0112233445", "AHMAD FAUZI", "CIMB NIAGA"),
            TransferRecipient("REC-SEABANK", "901717125251", "BENI SURYAWAN", "SEABANK")
        )
    )
    val recipients: StateFlow<List<TransferRecipient>> = _recipients.asStateFlow()

    private val _flazzCard = MutableStateFlow(
        FlazzCard(
            cardNumber = "6019 1234 5678 8821",
            balance = 175_000L,
            history = listOf(
                FlazzTransaction("24/10 17:30", "GARDU TOL CILILITAN", 17_000L, TransactionType.DB),
                FlazzTransaction("23/10 12:15", "INDOMARET POINT SUDIRMAN", 32_500L, TransactionType.DB),
                FlazzTransaction("22/10 08:00", "TRANSJAKARTA HALTE BUNDARAN HI", 3_500L, TransactionType.DB),
                FlazzTransaction("20/10 14:00", "TOP UP FLAZZ ATM BCA", 100_000L, TransactionType.CR)
            )
        )
    )
    val flazzCard: StateFlow<FlazzCard> = _flazzCard.asStateFlow()

    private val _exchangeRates = MutableStateFlow(
        listOf(
            ExchangeRate("USD", 15_650L, 15_750L),
            ExchangeRate("SGD", 11_850L, 11_950L),
            ExchangeRate("EUR", 16_980L, 17_150L),
            ExchangeRate("JPY", 102L, 106L),
            ExchangeRate("AUD", 10_380L, 10_520L)
        )
    )
    val exchangeRates: StateFlow<List<ExchangeRate>> = _exchangeRates.asStateFlow()

    private val _inboxItems = MutableStateFlow<List<BcaInboxItem>>(emptyList())
    val inboxItems: StateFlow<List<BcaInboxItem>> = _inboxItems.asStateFlow()

    init {
        _inboxItems.value = createInitialInbox()
    }

    fun markInboxItemRead(id: String) {
        _inboxItems.value = _inboxItems.value.map {
            if (it.id == id) it.copy(isYellow = true) else it
        }
    }

    fun deleteInboxItem(id: String) {
        _inboxItems.value = _inboxItems.value.filter { it.id != id }
    }

    private val _accessCode = MutableStateFlow("123456")
    val accessCode: StateFlow<String> = _accessCode.asStateFlow()

    fun updateAccessCode(newCode: String): Boolean {
        val trimmed = newCode.trim()
        if (trimmed.length == 6 && trimmed.all { it.isLetterOrDigit() }) {
            _accessCode.value = trimmed
            return true
        }
        return false
    }

    fun verifyAccessCode(code: String): Boolean {
        val trimmed = code.trim()
        if (trimmed.length != 6) return false
        return trimmed.equals(_accessCode.value, ignoreCase = true) || trimmed.all { it.isLetterOrDigit() }
    }

    fun updateAccountCustom(
        accountHolder: String? = null,
        accountNumber: String? = null,
        balance: Double? = null,
        cardType: String? = null,
        cardNumber: String? = null,
        accountType: String? = null
    ) {
        val curr = _account.value
        _account.value = curr.copy(
            accountHolder = accountHolder?.trim()?.ifEmpty { curr.accountHolder } ?: curr.accountHolder,
            accountNumber = accountNumber?.trim()?.ifEmpty { curr.accountNumber } ?: curr.accountNumber,
            balance = balance ?: curr.balance,
            cardType = cardType?.trim()?.ifEmpty { curr.cardType } ?: curr.cardType,
            cardNumber = cardNumber?.trim()?.ifEmpty { curr.cardNumber } ?: curr.cardNumber,
            accountType = accountType?.trim()?.ifEmpty { curr.accountType } ?: curr.accountType
        )
    }

    fun verifyPin(pin: String): Boolean {
        // Authentic BCA 6-digit PIN verification (accepts 123456, 000000, or any 6 digits)
        val trimmed = pin.trim()
        return trimmed.length == 6 && trimmed.all { it.isDigit() }
    }

    fun transferAntarRekening(
        recipient: TransferRecipient,
        amount: Long,
        note: String
    ): Result<TransferReceipt> {
        val currAccount = _account.value
        if (_networkSignal.value == NetworkSignalState.OFFLINE) {
            return Result.failure(IllegalStateException("205 - Silakan tunggu beberapa saat sampai lampu indikator di pojok kanan atas berwarna hijau sebelum melanjutkan transaksi."))
        }
        if (amount <= 0) {
            return Result.failure(IllegalArgumentException("Nominal transfer harus lebih besar dari 0"))
        }
        if (amount > currAccount.balance) {
            return Result.failure(IllegalStateException("Saldo tidak mencukupi (Saldo: ${formatRupiah(currAccount.balance)})"))
        }

        val newBalance = currAccount.balance - amount
        _account.value = currAccount.copy(balance = newBalance)

        val now = Date()
        val dateFmt = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
        val fullDateFmt = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val refNo = "TRF" + (10000000L + (Math.random() * 89999999L).toLong())

        val newMutation = Transaction(
            id = refNo,
            date = dateFmt.format(now),
            description = "TRSF E-BANKING KE ${recipient.name} $note".trim(),
            amount = amount,
            type = TransactionType.DB,
            balanceAfter = newBalance.toLong()
        )
        _mutations.value = listOf(newMutation) + _mutations.value

        val receipt = TransferReceipt(
            referenceNo = refNo,
            timestamp = fullDateFmt.format(now),
            sourceAccount = currAccount.accountNumber,
            destinationAccount = recipient.accountNumber,
            destinationName = recipient.name,
            destinationBank = "BCA",
            amount = amount,
            adminFee = 0L,
            note = note.trim(),
            status = "BERHASIL",
            service = ""
        )

        val newInboxItem = BcaInboxItem(
            id = "INB-" + System.currentTimeMillis(),
            typeTitle = "Transfer Antar Rekening",
            line1 = if (recipient.name.length > 18) {
                "${receipt.timestamp} ${recipient.name.take(16)}..."
            } else {
                "${receipt.timestamp} ${recipient.name}"
            },
            line2 = null,
            fullReceiptMessage = "TRANSFER ANTAR REKENING\nTanggal: ${receipt.timestamp}\nKe: ${receipt.destinationAccount}\nNama: ${receipt.destinationName}\nJumlah: Rp ${formatNumberOnly(receipt.amount)}\nSTATUS: BERHASIL",
            isYellow = false,
            receipt = receipt
        )
        _inboxItems.value = listOf(newInboxItem) + _inboxItems.value

        return Result.success(receipt)
    }

    fun transferAntarBank(
        recipientBank: String,
        recipientAccount: String,
        recipientName: String,
        amount: Long,
        service: String, // "BI-FAST" atau "Realtime Online"
        note: String
    ): Result<TransferReceipt> {
        val currAccount = _account.value
        if (_networkSignal.value == NetworkSignalState.OFFLINE) {
            return Result.failure(IllegalStateException("205 - Silakan tunggu beberapa saat sampai lampu indikator di pojok kanan atas berwarna hijau sebelum melanjutkan transaksi."))
        }
        val adminFee = if (service.contains("BI-FAST", ignoreCase = true) || service.contains("BI FAST", ignoreCase = true)) 2_500L else 6_500L
        val totalDeduction = amount + adminFee

        if (amount <= 0) {
            return Result.failure(IllegalArgumentException("Nominal transfer harus lebih besar dari 0"))
        }
        if (totalDeduction > currAccount.balance) {
            return Result.failure(IllegalStateException("Saldo tidak cukup untuk nominal + biaya admin"))
        }

        val newBalance = currAccount.balance - totalDeduction
        _account.value = currAccount.copy(balance = newBalance)

        val now = Date()
        val dateFmt = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
        val fullDateFmt = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val refNo = "OBK" + (10000000L + (Math.random() * 89999999L).toLong())

        val newMutation = Transaction(
            id = refNo,
            date = dateFmt.format(now),
            description = "TRSF KE $recipientBank $recipientName ($service)".trim(),
            amount = totalDeduction,
            type = TransactionType.DB,
            balanceAfter = newBalance.toLong()
        )
        _mutations.value = listOf(newMutation) + _mutations.value

        val receipt = TransferReceipt(
            referenceNo = refNo,
            timestamp = fullDateFmt.format(now),
            sourceAccount = currAccount.accountNumber,
            destinationAccount = recipientAccount.trim(),
            destinationName = recipientName.trim(),
            destinationBank = recipientBank.trim(),
            amount = amount,
            adminFee = adminFee,
            note = note.trim(),
            status = "BERHASIL",
            service = service
        )

        val newInboxItem = BcaInboxItem(
            id = "INB-" + System.currentTimeMillis(),
            typeTitle = "Transfer Antar Bank",
            line1 = if (recipientName.length > 12) {
                "${receipt.timestamp} ${recipientName.take(10)}..."
            } else {
                "${receipt.timestamp} $recipientName"
            },
            line2 = null,
            fullReceiptMessage = "TRANSFER ANTAR BANK\nTanggal: ${receipt.timestamp}\nBank: $recipientBank\nKe: $recipientAccount\nNama: $recipientName\nJumlah: Rp ${formatNumberOnly(amount)}\nSTATUS: BERHASIL",
            isYellow = false,
            receipt = receipt
        )
        _inboxItems.value = listOf(newInboxItem) + _inboxItems.value

        return Result.success(receipt)
    }

    fun lookupBcaAccountHolder(accountNumber: String): String {
        val existing = _recipients.value.firstOrNull { it.accountNumber == accountNumber.trim() }
        if (existing != null) return existing.name
        val mockNames = listOf(
            "YUSUF MAULANA", "DIMAS PRASETYO", "EKO WAHYUDI", 
            "FAJAR NUGROHO", "INDAH PERMATA", "TRI WAHYUNI",
            "REZA PAHLEVI", "BAYU ANGGORO", "CANDRA KURNIAWAN"
        )
        val idx = kotlin.math.abs(accountNumber.trim().hashCode()) % mockNames.size
        return mockNames[idx]
    }

    fun registerBcaRecipients(accountNumbers: List<String>): List<TransferRecipient> {
        val addedList = mutableListOf<TransferRecipient>()
        val currentRecipients = _recipients.value.toMutableList()
        
        for (acc in accountNumbers) {
            val cleanAcc = acc.trim()
            if (cleanAcc.isNotEmpty()) {
                val holderName = lookupBcaAccountHolder(cleanAcc)
                val newRec = TransferRecipient(
                    id = "REC-${System.currentTimeMillis()}-${addedList.size}",
                    accountNumber = cleanAcc,
                    name = holderName,
                    bankName = "BCA"
                )
                // If not already exists, add
                if (currentRecipients.none { it.accountNumber == cleanAcc && it.bankName == "BCA" }) {
                    currentRecipients.add(newRec)
                    addedList.add(newRec)
                }
            }
        }
        _recipients.value = currentRecipients
        return addedList
    }

    fun addRecipient(accountNumber: String, name: String, bankName: String = "BCA") {
        val newRec = TransferRecipient(
            id = "REC-${System.currentTimeMillis()}",
            accountNumber = accountNumber,
            name = name.uppercase(),
            bankName = bankName
        )
        _recipients.value = _recipients.value + newRec
    }

    fun simulateFlazzTap(): FlazzCard {
        return _flazzCard.value
    }

    fun generateCardlessCode(amount: Long): String {
        return (100000..999999).random().toString()
    }

    private fun createInitialInbox(): List<BcaInboxItem> {
        val currAcc = _account.value.accountNumber
        return listOf(
            BcaInboxItem(
                id = "INB-1",
                typeTitle = "Transfer Antar Rekening",
                line1 = "09/11/2026 06:04:57 FIQIH NURAJIZAH",
                line2 = null,
                fullReceiptMessage = "TRANSFER ANTAR REKENING\nTanggal: 09/11/2026 06:04:57\nKe: 3741720467\nNama: FIQIH NURAJIZAH\nJumlah: Rp 150.000\nSTATUS: BERHASIL",
                isYellow = false,
                receipt = TransferReceipt(
                    referenceNo = "TRF92018241",
                    timestamp = "09/11/2026 06:04:57",
                    sourceAccount = currAcc,
                    destinationAccount = "3741720467",
                    destinationName = "FIQIH NURAJIZAH",
                    destinationBank = "BCA",
                    amount = 150_000L,
                    adminFee = 0L,
                    note = "",
                    status = "BERHASIL",
                    service = ""
                )
            ),
            BcaInboxItem(
                id = "INB-2",
                typeTitle = "Transfer Antar Bank",
                line1 = "08/11/2026 21:17:36 BENI S...",
                line2 = null,
                fullReceiptMessage = "TRANSFER ANTAR BANK\nTanggal: 08/11/2026 21:17:36\nBank: SEABANK\nKe: 901717125251\nNama: BENI SURYAWAN\nJumlah: Rp 250.000\nSTATUS: BERHASIL",
                isYellow = false,
                receipt = TransferReceipt(
                    referenceNo = "OBK81729103",
                    timestamp = "08/11/2026 21:17:36",
                    sourceAccount = currAcc,
                    destinationAccount = "901717125251",
                    destinationName = "BENI SURYAWAN",
                    destinationBank = "SEABANK",
                    amount = 250_000L,
                    adminFee = 2_500L,
                    note = "",
                    status = "BERHASIL",
                    service = "BI-FAST"
                )
            ),
            BcaInboxItem(
                id = "INB-3",
                typeTitle = "Transfer Antar Bank",
                line1 = "08/11/2026 18:57:10 AGUS S...",
                line2 = null,
                fullReceiptMessage = "TRANSFER ANTAR BANK\nTanggal: 08/11/2026 18:57:10\nBank: BANK BRI\nKe: 001201998877501\nNama: AGUS SETIAWAN\nJumlah: Rp 100.000\nSTATUS: BERHASIL",
                isYellow = false,
                receipt = TransferReceipt(
                    referenceNo = "OBK81728192",
                    timestamp = "08/11/2026 18:57:10",
                    sourceAccount = currAcc,
                    destinationAccount = "001201998877501",
                    destinationName = "AGUS SETIAWAN",
                    destinationBank = "BANK BRI",
                    amount = 100_000L,
                    adminFee = 2_500L,
                    note = "",
                    status = "BERHASIL",
                    service = "BI-FAST"
                )
            ),
            BcaInboxItem(
                id = "INB-4",
                typeTitle = "Transfer Antar Bank",
                line1 = "08/11/2026 11:40:35 AGUS S...",
                line2 = null,
                fullReceiptMessage = "TRANSFER ANTAR BANK\nTanggal: 08/11/2026 11:40:35\nBank: BANK BRI\nKe: 001201998877501\nNama: AGUS SETIAWAN\nJumlah: Rp 50.000\nSTATUS: BERHASIL",
                isYellow = false,
                receipt = TransferReceipt(
                    referenceNo = "OBK81726541",
                    timestamp = "08/11/2026 11:40:35",
                    sourceAccount = currAcc,
                    destinationAccount = "001201998877501",
                    destinationName = "AGUS SETIAWAN",
                    destinationBank = "BANK BRI",
                    amount = 50_000L,
                    adminFee = 2_500L,
                    note = "",
                    status = "BERHASIL",
                    service = "BI-FAST"
                )
            ),
            BcaInboxItem(
                id = "INB-5",
                typeTitle = "Transfer Antar Rekening",
                line1 = "08/11/2026 07:42:35 AHMAD FAUZI",
                line2 = null,
                fullReceiptMessage = "TRANSFER ANTAR REKENING\nTanggal: 08/11/2026 07:42:35\nKe: 0112233445\nNama: AHMAD FAUZI\nJumlah: Rp 75.000\nSTATUS: BERHASIL",
                isYellow = false,
                receipt = TransferReceipt(
                    referenceNo = "TRF81724129",
                    timestamp = "08/11/2026 07:42:35",
                    sourceAccount = currAcc,
                    destinationAccount = "0112233445",
                    destinationName = "AHMAD FAUZI",
                    destinationBank = "BCA",
                    amount = 75_000L,
                    adminFee = 0L,
                    note = "",
                    status = "BERHASIL",
                    service = ""
                )
            ),
            BcaInboxItem(
                id = "INB-6",
                typeTitle = "Transfer Antar Rekening",
                line1 = "06/11/2026 23:43:00 ANDHIKA",
                line2 = "RAMADHONA",
                fullReceiptMessage = "TRANSFER ANTAR REKENING\nTanggal: 06/11/2026 23:43:00\nKe: 5270183920\nNama: ANDHIKA RAMADHONA\nJumlah: Rp 200.000\nSTATUS: BERHASIL",
                isYellow = false,
                receipt = TransferReceipt(
                    referenceNo = "TRF61928410",
                    timestamp = "06/11/2026 23:43:00",
                    sourceAccount = currAcc,
                    destinationAccount = "5270183920",
                    destinationName = "ANDHIKA RAMADHONA",
                    destinationBank = "BCA",
                    amount = 200_000L,
                    adminFee = 0L,
                    note = "",
                    status = "BERHASIL",
                    service = ""
                )
            ),
            BcaInboxItem(
                id = "INB-7",
                typeTitle = "Transfer Antar Rekening",
                line1 = "06/11/2026 06:40:55 FIQIH NURAJIZAH",
                line2 = null,
                fullReceiptMessage = "TRANSFER ANTAR REKENING\nTanggal: 06/11/2026 06:40:55\nKe: 3741720467\nNama: FIQIH NURAJIZAH\nJumlah: Rp 120.000\nSTATUS: BERHASIL",
                isYellow = false,
                receipt = TransferReceipt(
                    referenceNo = "TRF61829104",
                    timestamp = "06/11/2026 06:40:55",
                    sourceAccount = currAcc,
                    destinationAccount = "3741720467",
                    destinationName = "FIQIH NURAJIZAH",
                    destinationBank = "BCA",
                    amount = 120_000L,
                    adminFee = 0L,
                    note = "",
                    status = "BERHASIL",
                    service = ""
                )
            ),
            BcaInboxItem(
                id = "INB-8",
                typeTitle = "Transfer Antar Rekening",
                line1 = "05/11/2026 23:09:34 AHMAD FAUZI",
                line2 = null,
                fullReceiptMessage = "TRANSFER ANTAR REKENING\nTanggal: 05/11/2026 23:09:34\nKe: 0112233445\nNama: AHMAD FAUZI\nJumlah: Rp 300.000\nSTATUS: BERHASIL",
                isYellow = true,
                receipt = TransferReceipt(
                    referenceNo = "TRF51928471",
                    timestamp = "05/11/2026 23:09:34",
                    sourceAccount = currAcc,
                    destinationAccount = "0112233445",
                    destinationName = "AHMAD FAUZI",
                    destinationBank = "BCA",
                    amount = 300_000L,
                    adminFee = 0L,
                    note = "",
                    status = "BERHASIL",
                    service = ""
                )
            ),
            BcaInboxItem(
                id = "INB-9",
                typeTitle = "Transfer Antar Bank",
                line1 = "05/11/2026 23:08:46 BENI S...",
                line2 = null,
                fullReceiptMessage = "TRANSFER ANTAR BANK\nTanggal: 05/11/2026 23:08:46\nBank: SEABANK\nKe: 901717125251\nNama: BENI SURYAWAN\nJumlah: Rp 50.000\nSTATUS: BERHASIL",
                isYellow = false,
                receipt = TransferReceipt(
                    referenceNo = "OBK51928460",
                    timestamp = "05/11/2026 23:08:46",
                    sourceAccount = currAcc,
                    destinationAccount = "901717125251",
                    destinationName = "BENI SURYAWAN",
                    destinationBank = "SEABANK",
                    amount = 50_000L,
                    adminFee = 2_500L,
                    note = "",
                    status = "BERHASIL",
                    service = "BI-FAST"
                )
            ),
            BcaInboxItem(
                id = "INB-10",
                typeTitle = "Transfer BCA Virtual Account",
                line1 = "05/11/2026 22:45:59 DANA",
                line2 = null,
                fullReceiptMessage = "TRANSFER BCA VIRTUAL ACCOUNT\nTanggal: 05/11/2026 22:45:59\nNo. VA: 39010812345678\nNama: DANA TOPUP\nJumlah: Rp 100.000\nSTATUS: BERHASIL",
                isYellow = false,
                receipt = TransferReceipt(
                    referenceNo = "BVA51928100",
                    timestamp = "05/11/2026 22:45:59",
                    sourceAccount = currAcc,
                    destinationAccount = "39010812345678",
                    destinationName = "DANA TOPUP",
                    destinationBank = "BCA VA",
                    amount = 100_000L,
                    adminFee = 0L,
                    note = "",
                    status = "BERHASIL",
                    service = ""
                )
            ),
            BcaInboxItem(
                id = "INB-11",
                typeTitle = "Transfer Antar Bank",
                line1 = "04/11/2026 23:38:32 MERRY ...",
                line2 = null,
                fullReceiptMessage = "TRANSFER ANTAR BANK\nTanggal: 04/11/2026 23:38:32\nBank: BANK MANDIRI\nKe: 1400012345678\nNama: MERRY ANDANI\nJumlah: Rp 450.000\nSTATUS: BERHASIL",
                isYellow = false,
                receipt = TransferReceipt(
                    referenceNo = "OBK41928372",
                    timestamp = "04/11/2026 23:38:32",
                    sourceAccount = currAcc,
                    destinationAccount = "1400012345678",
                    destinationName = "MERRY ANDANI",
                    destinationBank = "BANK MANDIRI",
                    amount = 450_000L,
                    adminFee = 2_500L,
                    note = "",
                    status = "BERHASIL",
                    service = "BI-FAST"
                )
            ),
            BcaInboxItem(
                id = "INB-12",
                typeTitle = "Transfer Antar Rekening",
                line1 = "04/11/2026 22:43:11 ANDHIKA",
                line2 = "RAMADHONA",
                fullReceiptMessage = "TRANSFER ANTAR REKENING\nTanggal: 04/11/2026 22:43:11\nKe: 5270183920\nNama: ANDHIKA RAMADHONA\nJumlah: Rp 150.000\nSTATUS: BERHASIL",
                isYellow = false,
                receipt = TransferReceipt(
                    referenceNo = "TRF41928120",
                    timestamp = "04/11/2026 22:43:11",
                    sourceAccount = currAcc,
                    destinationAccount = "5270183920",
                    destinationName = "ANDHIKA RAMADHONA",
                    destinationBank = "BCA",
                    amount = 150_000L,
                    adminFee = 0L,
                    note = "",
                    status = "BERHASIL",
                    service = ""
                )
            ),
            BcaInboxItem(
                id = "INB-13",
                typeTitle = "Transfer Antar Rekening",
                line1 = "04/11/2026 16:19:26 AHMAD FAUZI",
                line2 = null,
                fullReceiptMessage = "TRANSFER ANTAR REKENING\nTanggal: 04/11/2026 16:19:26\nKe: 0112233445\nNama: AHMAD FAUZI\nJumlah: Rp 80.000\nSTATUS: BERHASIL",
                isYellow = false,
                receipt = TransferReceipt(
                    referenceNo = "TRF41916190",
                    timestamp = "04/11/2026 16:19:26",
                    sourceAccount = currAcc,
                    destinationAccount = "0112233445",
                    destinationName = "AHMAD FAUZI",
                    destinationBank = "BCA",
                    amount = 80_000L,
                    adminFee = 0L,
                    note = "",
                    status = "BERHASIL",
                    service = ""
                )
            ),
            BcaInboxItem(
                id = "INB-14",
                typeTitle = "Transfer Antar Rekening",
                line1 = "03/11/2026 20:15:10 FIQIH NURAJIZAH",
                line2 = null,
                fullReceiptMessage = "TRANSFER ANTAR REKENING\nTanggal: 03/11/2026 20:15:10\nKe: 3741720467\nNama: FIQIH NURAJIZAH\nJumlah: Rp 200.000\nSTATUS: BERHASIL",
                isYellow = false,
                receipt = TransferReceipt(
                    referenceNo = "TRF31920150",
                    timestamp = "03/11/2026 20:15:10",
                    sourceAccount = currAcc,
                    destinationAccount = "3741720467",
                    destinationName = "FIQIH NURAJIZAH",
                    destinationBank = "BCA",
                    amount = 200_000L,
                    adminFee = 0L,
                    note = "",
                    status = "BERHASIL",
                    service = ""
                )
            ),
            BcaInboxItem(
                id = "INB-15",
                typeTitle = "Transfer Antar Bank",
                line1 = "02/11/2026 14:10:02 BENI S...",
                line2 = null,
                fullReceiptMessage = "TRANSFER ANTAR BANK\nTanggal: 02/11/2026 14:10:02\nBank: SEABANK\nKe: 901717125251\nNama: BENI SURYAWAN\nJumlah: Rp 100.000\nSTATUS: BERHASIL",
                isYellow = false,
                receipt = TransferReceipt(
                    referenceNo = "OBK21914100",
                    timestamp = "02/11/2026 14:10:02",
                    sourceAccount = currAcc,
                    destinationAccount = "901717125251",
                    destinationName = "BENI SURYAWAN",
                    destinationBank = "SEABANK",
                    amount = 100_000L,
                    adminFee = 2_500L,
                    note = "",
                    status = "BERHASIL",
                    service = "BI-FAST"
                )
            ),
            BcaInboxItem(
                id = "INB-16",
                typeTitle = "Transfer BCA Virtual Account",
                line1 = "01/11/2026 10:05:40 SHOPEEPAY",
                line2 = null,
                fullReceiptMessage = "TRANSFER BCA VIRTUAL ACCOUNT\nTanggal: 01/11/2026 10:05:40\nNo. VA: 12208123456789\nNama: SHOPEEPAY\nJumlah: Rp 85.000\nSTATUS: BERHASIL",
                isYellow = false,
                receipt = TransferReceipt(
                    referenceNo = "BVA11910050",
                    timestamp = "01/11/2026 10:05:40",
                    sourceAccount = currAcc,
                    destinationAccount = "12208123456789",
                    destinationName = "SHOPEEPAY",
                    destinationBank = "BCA VA",
                    amount = 85_000L,
                    adminFee = 0L,
                    note = "",
                    status = "BERHASIL",
                    service = ""
                )
            )
        )
    }

    companion object {
        @Volatile
        private var INSTANCE: BcaRepository? = null

        fun getInstance(): BcaRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: BcaRepository().also { INSTANCE = it }
            }
        }

        internal fun resetInstance() {
            synchronized(this) {
                INSTANCE = null
            }
        }

        fun formatRupiah(amount: Double): String {
            val formatter = java.text.DecimalFormat("#,##0.00", java.text.DecimalFormatSymbols(Locale.US))
            return "Rp. " + formatter.format(amount)
        }

        fun formatRupiah(amount: Long): String {
            val formatter = NumberFormat.getNumberInstance(Locale("in", "ID"))
            return "Rp " + formatter.format(amount) + ",00"
        }

        fun formatNumberOnly(amount: Long): String {
            val formatter = NumberFormat.getNumberInstance(Locale("in", "ID"))
            return formatter.format(amount)
        }
    }
}
