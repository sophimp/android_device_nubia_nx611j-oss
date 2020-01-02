package com.fingerprints.service;

interface IIfaaApi {
       byte[] processCmd(int cmd, int param1, int param2, in byte[] send_buf, int length);
}
