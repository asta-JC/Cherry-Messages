package com.asta.cherrymessages.ui.onboarding

import android.app.Application
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.asta.cherrymessages.data.repository.SmsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingUiState(
    val isImporting: Boolean = false,
    val importComplete: Boolean = false,
    val isDefaultSmsApp: Boolean = false,
    val permissionsGranted: Boolean = false
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    application: Application,
    private val smsRepository: SmsRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        checkDefaultSmsApp()
    }

    fun checkDefaultSmsApp() {
        val context = getApplication<Application>()
        val defaultSms = Telephony.Sms.getDefaultSmsPackage(context)
        val isDefault = defaultSms == context.packageName
        _uiState.update { it.copy(isDefaultSmsApp = isDefault) }
    }

    fun requestDefaultSmsApp(activityContext: Context) {
        val context = getApplication<Application>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = context.getSystemService(Context.ROLE_SERVICE) as? RoleManager
            if (roleManager?.isRoleAvailable(RoleManager.ROLE_SMS) == true) {
                if (!roleManager.isRoleHeld(RoleManager.ROLE_SMS)) {
                    val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_SMS)
                    (activityContext as? android.app.Activity)?.startActivityForResult(intent, 1001)
                }
            }
        } else {
            val intent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT).apply {
                putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, context.packageName)
            }
            activityContext.startActivity(intent)
        }
    }

    fun importSms() {
        viewModelScope.launch {
            _uiState.update { it.copy(isImporting = true) }
            try {
                smsRepository.syncSystemSms()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            _uiState.update { it.copy(isImporting = false, importComplete = true) }
        }
    }

    fun setPermissionsGranted(granted: Boolean) {
        _uiState.update { it.copy(permissionsGranted = granted) }
    }
}
