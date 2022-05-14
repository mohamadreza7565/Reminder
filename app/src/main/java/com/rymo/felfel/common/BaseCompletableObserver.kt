package com.rymo.felfel.common

import io.reactivex.CompletableObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus

abstract class BaseCompletableObserver(var compositeDisposable: CompositeDisposable) : CompletableObserver{
    override fun onSubscribe(d: Disposable) {
        compositeDisposable.add(d)
    }

}