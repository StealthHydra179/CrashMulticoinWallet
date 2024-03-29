package dev.einsjannis.crashwallet.server.wallet.transaction

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import dev.einsjannis.crashwallet.server.exceptions.AddressNotValidException
import dev.einsjannis.crashwallet.server.exceptions.NoUserDataFoundException
import dev.einsjannis.crashwallet.server.json.AddressSaveObject
import dev.einsjannis.crashwallet.server.wallet.address.Address
import dev.einsjannis.crashwallet.server.wallet.address.AddressType
import io.leonard.Base58
import java.io.File
import java.io.IOException

fun sendTransaction(type: AddressType, senderAddressUserId: Int, targetAddress: String, amount: Double): Boolean {
    return when(type){
        AddressType.BTC -> sendBTCTransaction(senderAddressUserId,targetAddress, amount)
        else -> false
    }
}

fun sendBTCTransaction(senderAddressUserId:Int, targetAddress: String, amount: Double): Boolean{
    if(!validateTargetAddress(targetAddress)) throw AddressNotValidException()
    val targetPublicKey = getPublicKeyFromAddress(targetAddress)
    val userdir = File("/data/addresses/btc/$senderAddressUserId")
    val addresses = mutableListOf<Address>()
    userdir.listFiles()?.forEach {
        val filetext = it.readText()
        val obj = jacksonObjectMapper().readValue<AddressSaveObject>(filetext)
        addresses.add(
			Address(obj.privateKey, obj.addrStr, AddressType.BTC)
		)
    }
    if(addresses.size == 0) throw IOException("")
    //Query UTXO for each address, get closest to amount to spent
    //Build Transaction
    //Calc fee
    //Finalize Transaction
    return false
}

fun getPublicKeyFromAddress(address: String): String {
    val base58decoded = Base58.decode(address)
    val droppedversionbyte = base58decoded.drop(1).toByteArray()
    val droppedchecksum = droppedversionbyte.dropLast(4).toByteArray()
    return droppedchecksum.toHexString()
}

fun validateTargetAddress(address: String): Boolean = address.length == 34 && address.startsWith("1")
