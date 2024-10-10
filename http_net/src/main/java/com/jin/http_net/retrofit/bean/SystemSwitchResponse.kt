package com.jin.http_net.retrofit.bean

class SystemSwitchResponse(val code: Int,val message: String,val content: SystemSwitch) {



    class SystemSwitch(val id: Int,val type: String,val codeVersion: String,val splashStatus: Int,val forceLogin: Int,val goPay: Int,val openScreenAd: String,val packageName: String,val insertScreenAd: String,val infoStreamAd: String,val insertScreenAdTime: Int,val wholeInterstitialTime: String,val showSign: String,val showBounced: String,val value: String) {
        override fun toString(): String {
            return "Content(id=$id, type='$type', codeVersion='$codeVersion', splashStatus=$splashStatus, forceLogin=$forceLogin, goPay=$goPay, openScreenAd='$openScreenAd', packageName='$packageName', insertScreenAd='$insertScreenAd', infoStreamAd='$infoStreamAd', insertScreenAdTime=$insertScreenAdTime, wholeInterstitialTime='$wholeInterstitialTime', showSign='$showSign', showBounced='$showBounced', value='$value')"
        }
    }

    override fun toString(): String {
        return "SystemSwitchResponse(code=$code, message='$message', content=$content)"
    }
}