package com.bikeshare.app.di

import com.bikeshare.app.data.repository.*
import com.bikeshare.app.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindStandRepository(impl: StandRepositoryImpl): StandRepository

    @Binds
    @Singleton
    abstract fun bindRentalRepository(impl: RentalRepositoryImpl): RentalRepository

    @Binds
    @Singleton
    abstract fun bindBikeRepository(impl: BikeRepositoryImpl): BikeRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindCouponRepository(impl: CouponRepositoryImpl): CouponRepository
}
