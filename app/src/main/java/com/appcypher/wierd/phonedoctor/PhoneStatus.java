package com.appcypher.wierd.phonedoctor;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by Nypro on 27-Mar-17.
 */

public class PhoneStatus {

	public PhoneStatus(){}

	public static int getNumberOfCores(){
		class CpuFilter implements FileFilter {
			@Override
			public boolean accept(File pathname) {
				//Check if filename is "cpu", followed by a single digit number
				if(Pattern.matches("cpu[0-9]+", pathname.getName())) { return true; }
				return false;
			}
		}

		try {
			File dir = new File("/sys/devices/system/cpu/");
			File[] files = dir.listFiles(new CpuFilter());
			return files.length;
		} catch(Exception e) {
			return 1;
		}
	}

	public static String getBuildBunch(){
		StringBuilder bunch = new StringBuilder();
		bunch.append("Hardware: ").append(Build.HARDWARE).append("\n");
		bunch.append("Brand: ").append(Build.BRAND).append("\n");
		bunch.append("Manufacturer: ").append(Build.MANUFACTURER).append("\n");
		bunch.append("Device: ").append(Build.DEVICE).append("\n");
		bunch.append("Product: ").append(Build.PRODUCT).append("\n");
		return bunch.toString();
	}

	public static int[] getCoresFrequencyCurrent(){
		int[] output = new int[getNumberOfCores()];
		for(int i=0; i<getNumberOfCores(); i++) {
			output[i] = readSystemFileAsInt("/sys/devices/system/cpu/cpu" + String.valueOf(i) + "/cpufreq/scaling_cur_freq");
		}
		return output;
	}

	public static String getCPUArchitecture(){
		return System.getProperty("os.arch");
	}

	public static long getAvailableRAM(Context context){ // TODO
		ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
		ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
		activityManager.getMemoryInfo(memoryInfo);
		return memoryInfo.availMem / 0x100000;
	}


	public static long getTotalRAM(Context context){
		ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
		ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
		activityManager.getMemoryInfo(memoryInfo);
		return memoryInfo.totalMem / 0x100000;
		// API specialiation needed. Reading /proc/meminfo for < API16
	}

	private static int readSystemFileAsInt(String filePath) {
		try{
			Scanner sc = new Scanner(new File(filePath));
			sc.close();
			return sc.nextInt();
		}catch(IOException ioe){ ioe.printStackTrace(); }
		return -1;
	}

	public static String compose(Context ctx){
		StringBuilder str = new StringBuilder();

		int numberOfCores = getNumberOfCores();
		int [] coresFrequency = getCoresFrequencyCurrent();
		long availableRAM = getAvailableRAM(ctx);
		long totalRAM = getTotalRAM(ctx);
		List<Float> coreLoads = getCoreLoads();
		String arch = getCPUArchitecture();
		String bunch = getBuildBunch();

		str.append(bunch).append("\n");

		str.append("CPU Architecture: ").append(arch).append("\n");

		str.append(numberOfCores).append(" cores\n");

		for(int i = 0; i < coresFrequency.length; i++){
			if(coresFrequency[i] == -1){
				str.append("Core").append(i).append("Freq = ").append("Unknown\n");
			}else
				str.append("Core").append(i).append("Freq = ").append(coresFrequency[i]).append("MHz\n");
		}
		for(int i = 0; i < coreLoads.size(); i++){
			if(coreLoads.get(i) == -1){
				str.append("Core").append(i).append("Load = ").append("Unknown\n");
			}else
				str.append("Core").append(i).append("Load = ").append(coreLoads.get(i)).append("\n");
		}

		str.append(availableRAM).append(" available RAM\n");
		str.append(totalRAM).append(" total RAM\n");
		return str.toString();
	}

	private static List<Float> getCoreLoads(){
		List<Float> loads = new ArrayList<>();
		for(int i = 0; i < getNumberOfCores(); i++){
			loads.add(readCore(i));
		}
		return loads;
	}

	// Needs work !
	// for multi core value
	private static float readCore(int i) {
    /*
     * how to calculate multicore this function reads the bytes from a
     * logging file in the android system (/proc/stat for cpu values) then
     * puts the line into a string then spilts up each individual part into
     * an array then(since he know which part represents what) we are able
     * to determine each cpu total and work then combine it together to get
     * a single float for overall cpu usage
     */
		try {
			RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
			// skip to the line we need
			for (int ii = 0; ii < i + 1; ++ii) {
				String line = reader.readLine();
			}
			String load = reader.readLine();

			// cores will eventually go offline, and if it does, then it is at
			// 0% because it is not being
			// used. so we need to do check if the line we got contains cpu, if
			// not, then this core = 0
			if (load.contains("cpu")) {
				String[] toks = load.split(" ");

				// we are recording the work being used by the user and
				// system(work) and the total info
				// of cpu stuff (total)
				// http://stackoverflow.com/questions/3017162/how-to-get-total-cpu-usage-in-linux-c/3017438#3017438

				long work1 = Long.parseLong(toks[1]) + Long.parseLong(toks[2])
						+ Long.parseLong(toks[3]);
				long total1 = Long.parseLong(toks[1]) + Long.parseLong(toks[2])
						+ Long.parseLong(toks[3]) + Long.parseLong(toks[4])
						+ Long.parseLong(toks[5]) + Long.parseLong(toks[6])
						+ Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

				try {
					// short sleep time = less accurate. But android devices
					// typically don't have more than
					// 4 cores, and I'n my app, I run this all in a second. So,
					// I need it a bit shorter
					Thread.sleep(300);
				} catch (Exception e) {
				}

				reader.seek(0);
				// skip to the line we need
				for (int ii = 0; ii < i + 1; ++ii) {
					reader.readLine();
				}
				load = reader.readLine();

				// cores will eventually go offline, and if it does, then it is
				// at 0% because it is not being
				// used. so we need to do check if the line we got contains cpu,
				// if not, then this core = 0%
				if (load.contains("cpu")) {
					reader.close();
					toks = load.split(" ");

					long work2 = Long.parseLong(toks[1]) + Long.parseLong(toks[2])
							+ Long.parseLong(toks[3]);
					long total2 = Long.parseLong(toks[1]) + Long.parseLong(toks[2])
							+ Long.parseLong(toks[3]) + Long.parseLong(toks[4])
							+ Long.parseLong(toks[5]) + Long.parseLong(toks[6])
							+ Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

					// here we find the change in user work and total info, and
					// divide by one another to get our total
					// seems to be accurate need to test on quad core
					// http://stackoverflow.com/questions/3017162/how-to-get-total-cpu-usage-in-linux-c/3017438#3017438

					if ((total2 - total1) == 0)
						return -1;
					else
						return (float) (work2 - work1) / ((total2 - total1));

				} else {
					reader.close();
					return -1;
				}

			} else {
				reader.close();
				return -1;
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return -1;
	}
}
