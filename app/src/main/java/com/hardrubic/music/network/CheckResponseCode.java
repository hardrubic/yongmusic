package com.hardrubic.music.network;

import com.hardrubic.music.network.response.BaseResponse;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.functions.Function;

public class CheckResponseCode<T extends BaseResponse> implements SingleTransformer<BaseResponse, T> {

    @Override
    public SingleSource<T> apply(Single<BaseResponse> upstream) {
        return upstream.flatMap(new Function<BaseResponse, SingleSource<T>>() {
            @Override
            public SingleSource<T> apply(BaseResponse baseResponse) {
                if (baseResponse.getCode() != 200) {
                    return Single.error(new Exception(String.valueOf(baseResponse.getCode())));
                }
                return (SingleSource<T>) Single.just(baseResponse);
            }
        });
    }
}
