package com.android.imagesearch.di

import android.content.Context
import android.content.SharedPreferences
import com.android.imagesearch.ui.util.Constants
import com.android.imagesearch.api.KaKaoSearchApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module // 해당 클래스가 Dagger Hilt 모듈임을 나타냄
@InstallIn(SingletonComponent::class) // 해당 모듈이 애플리케이션 전체에서 사용되는 싱글톤 컴포넌트에 설치되어야 한다는 것을 나타냄
class AppModuleClass {

    // 로깅에 사용할 OkHttp 클라이언트를 주입하는 ProvideOkHttpClient
    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    // Retrofit 객체를 작성하는 ProvideRetrofit
    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .baseUrl(Constants.BASE_URL)
            .build()
    }

    //KaKaoSearchApi Service 객체를 작성하는 KaKaoSearchApiService
    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): KaKaoSearchApi {
        return retrofit.create(KaKaoSearchApi::class.java)
    }

    // provideSharedPreferences 메서드에서 생성되는 SharedPreferences 객체가 애플리케이션 전체에서 단일 인스턴스로 유지되어야 함
    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(Constants.PREFERENCE_NAME, 0)
    }
}