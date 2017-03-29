package com.appcypher.wierd.phonedoctor;

import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

	TextView displayText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		List<PhoneData> phoneData = getMockData();

		RecyclerView recyclerView = (RecyclerView) findViewById(R.id.main_list);
		DetailAdapter adapter = new DetailAdapter(this, phoneData);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
			@Override
			public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
				super.getItemOffsets(outRect, view, parent, state);
				outRect.bottom = getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin);
			}
		});
		recyclerView.setAdapter(adapter);
	}

	public void buttonClicked(View v){
//		displayText = (TextView) findViewById(R.id.display_text);
//		displayText.setText(PhoneStatus.compose(this));
	}


	public List<PhoneData> getMockData(){
		List<PhoneData> allData = new ArrayList<>();

		// data1
		List<Pair<String, String>> detail1 = new ArrayList<>();
		detail1.add(new Pair<>("Chipset", "Intel Core i5" )); // "Qualcomm Snapdragon 410, 28nm"
		detail1.add(new Pair<>("Processor Design", "Unknown")); // Cortex A53
		detail1.add(new Pair<>("Processor Architecture", "Unknown" )); // ARMV7L
		detail1.add(new Pair<>("CPU Cores", "4 \u00d7 1750MHz" ));
		detail1.add(new Pair<>("Current Frequency", "Core1\t1501MHz    Core2\t1250MHz"));
		detail1.add(new Pair<>("", "Core3\t1210MHz    Core4\t1250MHz"));
		detail1.add(new Pair<>("CPU Load", "34%"));
		detail1.add(new Pair<>("CPU Temperature", "45 \u00B0C"));
		PhoneData data1 =  new PhoneData(
			R.drawable.cpu, "CPU", "\u2022 200MHz - 1.75GHz\n\u2022 4 \u00d7\tCores\n\u2022 34%\tLoad",
			R.drawable.cpu, detail1
		);

		// data1
		List<Pair<String, String>> detail2 = new ArrayList<>();
		detail2.add(new Pair<>("RAM Installed", "4 GB" ));
		detail2.add(new Pair<>("Usable Memory", "3.85 GB" ));
		detail2.add(new Pair<>("Free Memory", "545 MB" ));
		detail2.add(new Pair<>("Used Memory", "3.2 GB" ));
		detail2.add(new Pair<>("Buffers", "Unknown" )); // 12 MB
		PhoneData data2 =  new PhoneData(
				R.drawable.ram, "Memory", "\u2022 545 MB Available\n\u2022 3.85 GB\tTotal\n\u2022 3.2\tGB Used",
				R.drawable.ram, detail2
		);


		// data1
		List<Pair<String, String>> detail3 = new ArrayList<>();
		detail3.add(new Pair<>("Charge Remaining", "50%" ));
		detail3.add(new Pair<>("Power Source", "Battery"));
		detail3.add(new Pair<>("Charging Status", "Discharging"));
		detail3.add(new Pair<>("Charge Capacity", "Unknown")); // 6000 mAh
		detail3.add(new Pair<>("Output Voltage", "Unknown")); // 4070 mV
		detail3.add(new Pair<>("Battery Temperature", "Battery"));
		detail3.add(new Pair<>("Battery Technology", "Unknown")); // Li-ion
		detail3.add(new Pair<>("Battery Health", "Good"));
		PhoneData data3 =  new PhoneData(
				R.drawable.battery, "Battery", "\u2022 50% Remaining\n\u2022 Good Health\n\u2022 Charging",
				R.drawable.battery, detail3
		);

		allData.add(data1);
		allData.add(data2);
		allData.add(data3);
		return allData;

	}
}
