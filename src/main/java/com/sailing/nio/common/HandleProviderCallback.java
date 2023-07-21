package com.sailing.nio.common;

public abstract class HandleProviderCallback implements Runnable {

        @Override
        public final void run() {
            onProviderIo();
        }

        /**
         * 可以进行接收或者发送时的回调
         *
         */
        protected abstract void onProviderIo();

    }