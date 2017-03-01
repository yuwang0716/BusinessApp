package com.liuhesan.app.businessapp.ui.personcenter;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import com.liuhesan.app.businessapp.R;
import com.liuhesan.app.businessapp.service.PrintDataService;
import com.liuhesan.app.businessapp.utility.AppManager;
import com.liuhesan.app.businessapp.utility.StringToList;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BluetoothPrintActivity extends AppCompatActivity {
    private final static String TAG = "BluetoothPrint";
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter
            .getDefaultAdapter();
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
    private IntentFilter intentFilter;
    private BluetoothReceiver bluetoothReceiver;
    private List mList = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_print);
        AppManager.getAppManager().addActivity(BluetoothPrintActivity.this);
        ButterKnife.bind(this);
        bluetoothReceiver = new BluetoothReceiver();
        initView();
        unbondDevices = new ArrayList<>();
        bondDevices = new ArrayList<>();
        mList = new ArrayList();
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();

        for (BluetoothDevice b:bondedDevices) {
           addBandDevices(b);
        }
        addBondDevicesToListView();

    }

    @OnClick({ R.id.bluetooth_switch})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bluetooth_switch:
                if (bluetoothSwitch.isChecked()) {
                    openBluetooth(BluetoothPrintActivity.this);
                    expand.setVisibility(View.VISIBLE);
                } else {
                    closeBluetooth();
                    expand.setVisibility(View.GONE);
                }
                break;
        }
    }

    /**
     * 初始化界面
     */
    public void initView() {
        mToolbar.setNavigationIcon(R.mipmap.mine_back);
        mToolbar.setNavigationOnClickListener(v -> {
            finish();
            if (bluetoothAdapter.getState() == BluetoothAdapter.STATE_ON )
            bluetoothAdapter.cancelDiscovery();
        });
        // 设置广播信息过滤
        intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetoothReceiver,intentFilter);
        if (isOpen()) {
            bluetoothSwitch.setChecked(true);
            expand.setVisibility(View.VISIBLE);
        }
        if (!isOpen()) {
            bluetoothSwitch.setChecked(false);
            expand.setVisibility(View.GONE);
        }
        searchDevices = (Button) findViewById(R.id.searchDevices);
        searchDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOpen()) {
                    searchDevices();
                }
            }
        });
    }

    /**
     * 添加已绑定蓝牙设备到ListView
     */
    private void addBondDevicesToListView() {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        int count = this.bondDevices.size();
        System.out.println("已绑定设备数量：" + count);
        SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        String deviceAddress = sharedPreferences.getString("deviceAddress", "");
        for (int i = 0; i < count; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("deviceName", this.bondDevices.get(i).getName()+"\t"+this.bondDevices.get(i).getAddress());
            if (this.bondDevices.get(i).getAddress().equals(deviceAddress)){
                map.put("isConnect","已连接");
            }else {
                map.put("isConnect","未连接");
            }
            data.add(map);// 把item项的数据加到data中
        }


        String[] from = {"deviceName","isConnect"};
        int[] to = {R.id.device_name,R.id.tv_setting_print};
        SimpleAdapter simpleAdapter = new SimpleAdapter(BluetoothPrintActivity.this, data,
                R.layout.bonddevice_item, from, to);
        bondDevicesListView.setAdapter(simpleAdapter);
        bondDevicesListView
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int arg2, long arg3) {
                        BluetoothDevice device = null;
                        String address = null;
                        if (bondDevices.size() >0){
                            device = bondDevices.get(arg2);
                            address = device.getAddress();
                        }
                        TextView tv_connect = (TextView) arg1.findViewById(R.id.tv_setting_print);
                        if (tv_connect.getText().equals("未连接")){
                            PrintDataService printDataService = new PrintDataService(BluetoothPrintActivity.this, address);
                            boolean connect = printDataService.connect();
                            if (connect) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("deviceAddress", device.getAddress());
                                editor.putBoolean("isConnection", true);
                                editor.commit();
                                addBondDevicesToListView();

                            } else {
                                tv_connect.setText("未连接");
                            }

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
            map.put("deviceName", this.unbondDevices.get(i).getName()+"\t"+this.unbondDevices.get(i).getAddress());
            data.add(map);// 把item项的数据加到data中
        }
        String[] from = {"deviceName"};
        int[] to = {R.id.undevice_name};
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

    class BluetoothReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e(TAG, action+"onReceive: " );
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                searching.setVisibility(View.VISIBLE);
            }else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    addBandDevices(device);
                } else {
                    addUnbondDevices(device);
                }
            }  else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                    .equals(action)) {
                searching.setVisibility(View.GONE);
                addUnbondDevicesToListView();
                addBondDevicesToListView();
                bluetoothAdapter.cancelDiscovery();

            }

        }

    }

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
            mList.add(device.getName()+"\t"+device.getAddress());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothAdapter.cancelDiscovery();
        unregisterReceiver(bluetoothReceiver);
        bondDevices.clear();
    }
}
