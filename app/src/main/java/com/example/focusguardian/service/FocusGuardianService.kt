package com.example.focusguardian.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

class FocusGuardianService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        // Basic keyword blocking for 18+ content
        val blockedKeywords = listOf(
            "porn", "xxx", "sex", "nude", "erotic", "adult", "18+"
        )

        val text = event.text.joinToString(" ").lowercase()
        val contentDesc = event.contentDescription?.toString()?.lowercase() ?: ""
        
        val detected = blockedKeywords.any { keyword ->
            text.contains(keyword) || contentDesc.contains(keyword)
        }

        if (detected) {
            // Block action: Go Home
            performGlobalAction(GLOBAL_ACTION_HOME)
            
            // Optionally show a toast (need a Handler for Toast on main thread)
            // But for now, silent block is fine or maybe improved later.
        }
    }

    override fun onInterrupt() {
        // Service interrupted
    }
}
