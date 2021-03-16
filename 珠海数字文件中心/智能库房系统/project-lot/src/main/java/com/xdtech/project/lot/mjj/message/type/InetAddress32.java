package com.xdtech.project.lot.mjj.message.type;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class InetAddress32 extends Type {

    public InetAddress32() {

        try {
            InetAddress address = InetAddress.getLocalHost();
            load(address);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

    }

    public InetAddress32(InetAddress address) {
        load(address.getAddress());
    }

    public InetAddress32(byte[] value) {
        load(value);
    }

    public void load(InetAddress address) {
        load(address.getAddress());
    }

    public InetAddress toAddress() {
        try {
            return InetAddress.getByAddress(toBytes());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int sizeof() {
        return 4;
    }

}
