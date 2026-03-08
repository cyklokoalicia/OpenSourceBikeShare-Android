package com.bikeshare.app.data.api

import com.bikeshare.app.data.api.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ── Auth ────────────────────────────────────────────────────────────
    @POST("auth/token")
    suspend fun login(@Body body: TokenRequest): Response<ApiEnvelope<AuthTokens>>

    @POST("auth/refresh")
    suspend fun refresh(@Body body: RefreshRequest): Response<ApiEnvelope<AuthTokens>>

    @POST("auth/logout")
    suspend fun logout(@Body body: LogoutRequest): Response<ApiEnvelope<Any>>

    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequest): Response<ApiEnvelope<RegisterResponse>>

    @GET("auth/cities")
    suspend fun getCities(): Response<ApiEnvelope<List<String>>>

    // ── User (authenticated) ─────────────────────────────────────────────
    @POST("user/phone-confirm/request")
    suspend fun phoneConfirmRequest(): Response<ApiEnvelope<PhoneConfirmRequestResponse>>

    @POST("user/phone-confirm/verify")
    suspend fun phoneConfirmVerify(@Body body: PhoneConfirmVerifyRequest): Response<ApiEnvelope<Any>>

    // ── Stands (public) ─────────────────────────────────────────────────
    @GET("stands/markers")
    suspend fun getStandMarkers(): Response<ApiEnvelope<List<StandMarkerDto>>>

    @GET("stands/{standName}/bikes")
    suspend fun getStandBikes(
        @Path("standName") standName: String,
    ): Response<ApiEnvelope<StandBikesResponse>>

    // ── Rentals ─────────────────────────────────────────────────────────
    @POST("rentals")
    suspend fun rentBike(@Body body: RentRequest): Response<ApiEnvelope<RentSystemResultDto>>

    @POST("returns")
    suspend fun returnBike(@Body body: ReturnRequest): Response<ApiEnvelope<RentSystemResultDto>>

    // ── Me ───────────────────────────────────────────────────────────────
    @GET("me/bikes")
    suspend fun getMyBikes(): Response<ApiEnvelope<List<RentedBikeDto>>>

    @GET("me/limits")
    suspend fun getMyLimits(): Response<ApiEnvelope<UserLimitsDto>>

    @GET("me/credit-history")
    suspend fun getCreditHistory(): Response<ApiEnvelope<List<CreditHistoryItemDto>>>

    @GET("me/trips")
    suspend fun getMyTrips(): Response<ApiEnvelope<List<TripItemDto>>>

    @PATCH("me/city")
    suspend fun changeCity(@Body body: ChangeCityRequest): Response<ApiEnvelope<Any>>

    // ── Coupons ─────────────────────────────────────────────────────────
    @POST("coupons/redeem")
    suspend fun redeemCoupon(@Body body: RedeemCouponRequest): Response<ApiEnvelope<Any>>

    // ── Admin: Stands ───────────────────────────────────────────────────
    @GET("admin/stands")
    suspend fun getAdminStands(): Response<ApiEnvelope<List<StandDetailDto>>>

    @GET("admin/stands/{standName}")
    suspend fun getAdminStand(
        @Path("standName") standName: String,
    ): Response<ApiEnvelope<StandDetailDto>>

    @DELETE("admin/stands/{standName}/notes")
    suspend fun deleteStandNotes(
        @Path("standName") standName: String,
    ): Response<ApiEnvelope<Any>>

    // ── Admin: Bikes ────────────────────────────────────────────────────
    @GET("admin/bikes")
    suspend fun getAdminBikes(): Response<ApiEnvelope<List<BikeDetailDto>>>

    @GET("admin/bikes/{bikeNumber}")
    suspend fun getAdminBike(
        @Path("bikeNumber") bikeNumber: Int,
    ): Response<ApiEnvelope<BikeDetailDto>>

    @GET("admin/bikes/{bikeNumber}/last-usage")
    suspend fun getBikeLastUsage(
        @Path("bikeNumber") bikeNumber: Int,
    ): Response<ApiEnvelope<BikeLastUsageDto>>

    @GET("admin/bikes/{bikeNumber}/trip")
    suspend fun getBikeTrip(
        @Path("bikeNumber") bikeNumber: Int,
    ): Response<ApiEnvelope<List<BikeTripPointDto>>>

    @PATCH("admin/bikes/{bikeNumber}/lock-code")
    suspend fun setBikeLockCode(
        @Path("bikeNumber") bikeNumber: Int,
        @Body body: SetLockCodeRequest,
    ): Response<ApiEnvelope<Any>>

    @DELETE("admin/bikes/{bikeNumber}/notes")
    suspend fun deleteBikeNotes(
        @Path("bikeNumber") bikeNumber: Int,
    ): Response<ApiEnvelope<Any>>

    // ── Admin: Force Rent/Return ────────────────────────────────────────
    @POST("admin/rentals/force")
    suspend fun forceRent(@Body body: ForceRentRequest): Response<ApiEnvelope<Any>>

    @POST("admin/returns/force")
    suspend fun forceReturn(@Body body: ForceReturnRequest): Response<ApiEnvelope<Any>>

    @POST("admin/reverts")
    suspend fun revertBike(@Body body: RevertRequest): Response<ApiEnvelope<Any>>

    // ── Admin: Users ────────────────────────────────────────────────────
    @GET("admin/users")
    suspend fun getAdminUsers(): Response<ApiEnvelope<List<UserDto>>>

    @GET("admin/users/{userId}")
    suspend fun getAdminUser(@Path("userId") userId: Int): Response<ApiEnvelope<UserDto>>

    @PATCH("admin/users/{userId}")
    suspend fun updateUser(
        @Path("userId") userId: Int,
        @Body body: Map<String, @JvmSuppressWildcards Any>,
    ): Response<ApiEnvelope<Any>>

    @PUT("admin/users/{userId}/credit")
    suspend fun addUserCredit(
        @Path("userId") userId: Int,
        @Body body: AddCreditRequest,
    ): Response<ApiEnvelope<Any>>

    // ── Admin: Coupons ──────────────────────────────────────────────────
    @GET("admin/coupons")
    suspend fun getAdminCoupons(): Response<ApiEnvelope<List<CouponDto>>>

    @POST("admin/coupons/generate")
    suspend fun generateCoupons(@Body body: GenerateCouponsRequest): Response<ApiEnvelope<Any>>

    @POST("admin/coupons/{coupon}/sell")
    suspend fun sellCoupon(@Path("coupon") coupon: String): Response<ApiEnvelope<Any>>

    // ── Admin: Reports ──────────────────────────────────────────────────
    @GET("admin/reports/daily")
    suspend fun getDailyReport(): Response<ApiEnvelope<List<DailyReportDto>>>

    @GET("admin/reports/users")
    suspend fun getUserReport(): Response<ApiEnvelope<List<UserReportDto>>>

    @GET("admin/reports/users/{year}")
    suspend fun getUserReportByYear(@Path("year") year: Int): Response<ApiEnvelope<List<UserReportDto>>>

    @GET("admin/reports/inactive-bikes")
    suspend fun getInactiveBikesReport(): Response<ApiEnvelope<List<InactiveBikeDto>>>
}
