package com.snaggly.ksw_toolkit.core.service.mcu.parser

import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes

object CarDataEvent : ICarDataEvent() {
    override fun getCarDataEvent(data: ByteArray): EventManagerTypes {
        if (data[0] == 0x10.toByte()) {
            lightEvent.getCarDataEvent(data)
        }
        return EventManagerTypes.CarData
    }
}