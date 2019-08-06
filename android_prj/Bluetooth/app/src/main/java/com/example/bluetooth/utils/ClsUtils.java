package com.example.bluetooth.utils;

import android.bluetooth.BluetoothDevice;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ClsUtils {

    public static boolean createBond(BluetoothDevice device) {
        try {
            Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
            Boolean ret = (Boolean) createBondMethod.invoke(device);
            return ret.booleanValue();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean removeBond(BluetoothDevice device) {
        try {
            Method createBondMethod = BluetoothDevice.class.getMethod("removeBond");
            Boolean ret = (Boolean) createBondMethod.invoke(device);
            return ret.booleanValue();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean setPin(BluetoothDevice device, String string) {
        try {
            Method setPin = BluetoothDevice.class.getDeclaredMethod("setPin",
                    new Class[] {byte[].class});
            Boolean ret = (Boolean) setPin.invoke(device,
                    new Object[]{string.getBytes()});
            return ret.booleanValue();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean cancelBondProcess(BluetoothDevice device) {
        try {
            Method cancelBondProcess = BluetoothDevice.class.getDeclaredMethod("cancelBondProcess");
            Boolean ret = (Boolean) cancelBondProcess.invoke(device);
            return ret.booleanValue();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void setPairingConfirmation(BluetoothDevice device, boolean isConform) {
        try {
            Method setPairingConfirmation = BluetoothDevice.class.getDeclaredMethod("setPairingConfirmation",
                    boolean.class);
            setPairingConfirmation.invoke(device, isConform);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
