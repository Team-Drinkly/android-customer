package com.project.drinkly.ui.subscribe.viewModel

import android.content.Context
import java.util.Calendar
import java.util.Date

object SubscriptionChecker {

    private const val PREF_NAME = "SubscriptionPrefs"
    private const val KEY_CHECK_DATE = "SUBSCRIPTION_CHECK_DATE"

    /**
     * 구독 상태 확인 날짜를 저장하는 함수
     * 기준: 정오(12시) 이전은 '어제'로 저장, 이후는 '오늘'로 저장
     * 저장 시 시간은 제외하고 날짜만 저장 (00:00 기준 timestamp)
     */
    fun saveSubscriptionLastCheckedDate(context: Context, date: Date = Date()) {
        val calendar = Calendar.getInstance().apply { time = date }
        val referenceDate: Date

        // 현재 날짜의 정오(12시)를 기준점으로 삼음
        calendar.set(Calendar.HOUR_OF_DAY, 12)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val noonToday = calendar.time

        referenceDate = if (date.before(noonToday)) {
            // 정오 이전이면 어제로 간주
            Calendar.getInstance().apply {
                time = date
                add(Calendar.DATE, -1)
            }.time
        } else {
            // 정오 이후면 오늘로 간주
            date
        }

        // 시간 정보 제거 → 자정 00:00 으로 통일
        val dateOnly = Calendar.getInstance().apply {
            time = referenceDate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
            .putLong(KEY_CHECK_DATE, dateOnly)
            .apply()
    }

    /**
     * 저장된 구독 상태 확인 날짜를 가져오는 함수
     */
    fun getSubscriptionLastCheckedDate(context: Context): Date? {
        val timestamp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getLong(KEY_CHECK_DATE, -1L)
        return if (timestamp != -1L) Date(timestamp) else null
    }

    /**
     * 저장된 구독 상태 확인 날짜를 삭제하는 함수
     */
    fun removeSubscriptionLastCheckedDate(context: Context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_CHECK_DATE)
            .apply()
    }


    /**
     * 오늘(또는 정오 기준 어제)에 구독 상태를 확인했는지 여부 반환
     */
    fun isSubscriptionCheckedToday(context: Context): Boolean {
        val savedDate = getSubscriptionLastCheckedDate(context) ?: return false
        val now = Date()
        val calendar = Calendar.getInstance()

        // 현재 기준 정오 계산
        calendar.time = now
        calendar.set(Calendar.HOUR_OF_DAY, 12)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val noonToday = calendar.time

        val referenceDate = if (now.before(noonToday)) {
            // 정오 이전 → 어제를 기준
            Calendar.getInstance().apply {
                time = now
                add(Calendar.DATE, -1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time
        } else {
            // 정오 이후 → 오늘을 기준
            Calendar.getInstance().apply {
                time = now
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time
        }

        // 저장된 날짜와 기준 날짜가 같은지 비교
        val savedCal = Calendar.getInstance().apply {
            time = savedDate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        return savedCal.time == referenceDate
    }
}
