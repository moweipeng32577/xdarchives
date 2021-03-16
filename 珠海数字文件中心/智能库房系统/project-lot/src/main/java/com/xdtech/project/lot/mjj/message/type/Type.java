package com.xdtech.project.lot.mjj.message.type;

public abstract class Type {

    protected byte[] value = new byte[sizeof()];

    public void load(byte[] data) {
        value = new byte[sizeof()];

        if (data.length < sizeof()) {
            for (int i = 0; i < value.length; i++) {
                if (i < (sizeof() - data.length)) {
                    value[i] = 0;
                } else {
                    value[i] = data[i - (value.length - data.length)];
                }
            }
        } else if (data.length > sizeof()) {
            for (int i = 0; i < data.length; i++) {
                if (i >= data.length - value.length) {
                    value[i - (data.length - value.length)] = data[i];
                }
            }
        } else {
            for (int i = 0; i < data.length; i++) {
                value[i] = data[i];
            }
        }

    }

    public String toHex() {

        StringBuffer hex = new StringBuffer("0x");
        byte[] b = toBytes();

        for (int i = 0; i < b.length; i++) {
            String s = Integer.toHexString(b[i] & 0x000000ff);
            if (s.length() == 1) {
                hex.append("0");
            }

            hex.append(s);
        }
        return hex.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Type) {
            Type type = (Type) obj;
            byte[] b1 = toBytes();
            byte[] b2 = type.toBytes();

            if (b1.length == b2.length) {

                for (int i = 0; i < b1.length; i++) {
                    if (b1[i] != b2[i]) {
                        return false;
                    }
                }

                return true;
            }


        }
        return false;
    }

    @Override
    public String toString() {
        return toHex();
    }

    public byte[] toBytes() {
        return value;
    }

    abstract public int sizeof();

    public static int SIZEOF(Class<?> clazz) {
        if (Type.class.isAssignableFrom(clazz)) {
            try {
                Object o = clazz.newInstance();
                return ((Type) o).sizeof();
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return 0;
    }

}
