package com.snaggly.ksw_toolkit.util.keyevent

import com.snaggly.ksw_toolkit.util.enums.EventMode

enum class KeyCode(val keycode : Int) {
    ASSIST(219),
    BACK(4),
    CALCULATOR( 210),
    CALENDAR( 208),
    CONTACTS( 207),
    DPAD_CENTER( 23),
    DPAD_DOWN( 20),
    DPAD_LEFT(  21),
    DPAD_RIGHT(22),
    DPAD_UP(19),
    ENTER(66),
    ENVELOPE( 65),
    EXPLORER(64),
    HOME(3),
    MEDIA_NEXT(87),
    MEDIA_PLAY_PAUSE(85),
    MEDIA_PREVIOUS(88),
    MEDIA_STOP(86),
    MUSIC(209),
    MUTE(91),
    SETTINGS(176),
    VOICE_ASSIST(231),
    VOLUME_DOWN(25),
    VOLUME_MUTE(164),
    VOLUME_UP(24);

    companion object {
        private val typeNames = EventMode.values().associateBy { it.name }
        fun findByName(name: String) = typeNames[name]
    }
}