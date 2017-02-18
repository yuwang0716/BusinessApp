package com.liuhesan.app.myapplication.ui.personcenter;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.liuhesan.app.myapplication.R;
import com.liuhesan.app.myapplication.service.PrintDataService;
import com.liuhesan.app.myapplication.utility.AppManager;
import com.liuhesan.app.myapplication.utility.StringToList;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BluetoothPrintActivity extends AppCompatActivity {
    private final static String TAG = "BluetoothPrint";
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter
            .getDefaultAdapter();
    @BindView(R.id.bluetooth_print_back)
    ImageButton back;
    @BindView(R.id.bluetooth_switch)
    CheckBox bluetoothSwitch;
    @BindView(R.id.bondDevices)
    ListView bondDevicesListView;
    @BindView(R.id.unbondDevices)
    ListView unbondDevicesListView;
    @BindView(R.id.searchDevices)
    Button searchDevices;
    @BindView(R.id.bluetooth_print_expand)
    LinearLayout expand;
    @BindView(R.id.searching)
    LinearLayout searching;
    private ArrayList<BluetoothDevice> unbondDevices = null; // 用于存放未配对蓝牙设备
    private List<BluetoothDevice> bondDevices = null;// 用于存放已配对蓝牙设备
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_print);
        AppManager.getAppManager().addActivity(BluetoothPrintActivity.this);
        ButterKnife.bind(this);
        initView();
        unbondDevices = new ArrayList<>();
        bondDevices = new ArrayList<>();
        SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        String devices = sharedPreferences.getString("devices", "");
        if (!TextUtils.isEmpty(devices)){
            try {
                bondDevices = StringToList.String2SceneList(devices);
                addBondDevicesToListView();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        searchDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, isOpen()+"onClick: ");
                if (isOpen()){
                    searchDevices();
                }
            }
        });
    }

    @OnClick({R.id.bluetooth_print_back, R.id.bluetooth_switch})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bluetooth_print_back:
                finish();
                break;
            case R.id.bluetooth_switch:
                if (bluetoothSwitch.isChecked()) {
                    openBluetooth(BluetoothPrintActivity.this);
                    expand.setVisibility(View.VISIBLE);
                } else {
                    closeBluetooth();
                    expand.setVisibility(View.GONE);
                }
                break;
            case R.id.searchDevices:
                Log.i(TAG, isOpen()+"onClick: ");
                if (isOpen()){
                    searchDevices();
                }
                break;
        }
    }

    /**
     * 初始化界面
     */
    public void initView() {
        // 设置广播信息过滤
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(receiver, intentFilter);
        if (isOpen()) {
            bluetoothSwitch.setChecked(true);
            expand.setVisibility(View.VISIBLE);
        }
        if (!isOpen()) {
            bluetoothSwitch.setChecked(false);
            expand.setVisibility(View.GONE);
        }
    }
    /**
     * 添加已绑定蓝牙设备到ListView
     */
    private void addBondDevicesToListView() {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        int count = this.bondDevices.size();
        System.out.println("已绑定设备数量：" + count);

        for (int i = 0; i < count; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("deviceName", this.bondDevices.get(i).getName());
            data.add(map);// 把item项的数据加到data中
        }
        String[] from = { "deviceName" };
        int[] to = { R.id.device_name };
        SimpleAdapter simpleAdapter = new SimpleAdapter(BluetoothPrintActivity.this, data,
                R.layout.bonddevice_item, from, to);
        bondDevicesListView.setAdapter(simpleAdapter);
        bondDevicesListView
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int arg2, long arg3) {
                        BluetoothDevice device = bondDevices.get(arg2);
                        PrintDataService printDataService = new PrintDataService(BluetoothPrintActivity.this,device.getAddress());
                        boolean connect = printDataService.connect();
                        TextView tv_connect = (TextView) arg1.findViewById(R.id.tv_setting_print);

                        if (connect){
                            tv_connect.setText("已连接");
                            SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("deviceAddress",  device.getAddress());
                            editor.putBoolean("isConnection",true);
                            editor.commit();

                        }else {
                            tv_connect.setText("未连接");
                        }


                    }
                });

    }

    /**
     * 添加未绑定蓝牙设备到ListView
     */
    private void addUnbondDevicesToListView() {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        int count = this.unbondDevices.size();
        for (int i = 0; i < count; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("deviceName", this.unbondDevices.get(i).getName());
            data.add(map);// 把item项的数据加到data中
        }
        String[] from = { "deviceName" };
        int[] to = { R.id.undevice_name };
        SimpleAdapter simpleAdapter = new SimpleAdapter(BluetoothPrintActivity.this, data,
                R.layout.unbonddevice_item, from, to);

        // 把适配器装载到listView中
        unbondDevicesListView.setAdapter(simpleAdapter);

        // 为每个item绑定监听，用于设备间的配对
        unbondDevicesListView
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int arg2, long arg3) {
                        try {
                            Method createBondMethod = BluetoothDevice.class
                                    .getMethod("createBond");
                            createBondMethod
                                    .invoke(unbondDevices.get(arg2));
                            // 将绑定好的设备添加的已绑定list集合
                            bondDevices.add(unbondDevices.get(arg2));
                            // 将绑定好的设备从未绑定list集合中移除
                            unbondDevices.remove(arg2);
                            addBondDevicesToListView();
                            addUnbondDevicesToListView();
                        } catch (Exception e) {
                            Toast.makeText(BluetoothPrintActivity.this, "配对失败！", Toast.LENGTH_SHORT)
                                    .show();
                        }

                    }
                });
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    addBandDevices(device);
                } else {
                    addUnbondDevices(device);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
               searching.setVisibility(View.VISIBLE);

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                    .equals(action)) {
                searching.setVisibility(View.GONE);
                addUnbondDevicesToListView();
                addBondDevicesToListView();
                context.unregisterReceiver(receiver);
            }

        }

    };
    /**
     * 打开蓝牙
     */
    public void openBluetooth(Activity activity) {
        Intent enableBtIntent = new Intent(
                BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(enableBtIntent, 1);

    }

    /**
     * 关闭蓝牙
     */
    public void closeBluetooth() {
        this.bluetoothAdapter.disable();

    }

    /**
     * 判断蓝牙是否打开
     *
     * @return boolean
     */
    public boolean isOpen() {
        return bluetoothAdapter.isEnabled();

    }

    /**
     * 搜索蓝牙设备
     */
    public void searchDevices() {
        // 寻找蓝牙设备，android会将查找到的设备以广播形式发出去
        bluetoothAdapter.startDiscovery();
    }




    /**
     * 添加未绑定蓝牙设备到list集合
     *
     * @param device
     */
    public void addUnbondDevices(BluetoothDevice device) {
        System.out.println("未绑定设备名称：" + device.getName());
        if (!unbondDevices.contains(device)) {
           unbondDevices.add(device);
        }
    }

    /**
     * 添加已绑定蓝牙设备到list集合
     *
     * @param device
     */
    public void addBandDevices(BluetoothDevice device) {
        System.out.println("已绑定设备名称：" + device.getName());
        if (!bondDevices.contains(device)) {
            bondDevices.add(device);
            String devices = null;
            try {
                devices = StringToList.SceneList2String(bondDevices);
                Log.i(TAG,devices+ "addBandDevices: ");
                SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putString("devices", devices);
                edit.commit();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
