import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class OrderMatcher {
	
	private List<Integer> buy_volumes;
	private List<Integer> buy_prices;
	private List<Integer> sell_volumes;
	private List<Integer> sell_prices;
	
	public OrderMatcher() {
		buy_volumes = new ArrayList<Integer>();
		buy_prices = new ArrayList<Integer>();
		sell_volumes = new ArrayList<Integer>();
		sell_prices = new ArrayList<Integer>();
	}
	
	private int find_index(int price, boolean buy) {
		int index = 0;
		if (buy) {
			index = Collections.binarySearch(buy_prices, price);
			if (index < 0) {
				index = -(index+1);
			} else {
				// find smaller end
				while(index >= 0) {
					if (index==0) break;
					if (buy_prices.get(index-1) != price) break;
					index--;
				}
			}
		} else {
			index = Collections.binarySearch(sell_prices, price);
			if (index < 0) {
				index = -(index+1);
			} else {
				// find larger end
				while(index <= sell_prices.size()-1) {
					if (index==sell_prices.size()-1) break;
					if (sell_prices.get(index+1) != price) break;
					index++;
				}
				index++;
			}
		}
		return index;
	}
	
	private boolean trade_occurs(boolean buy) {
		if (buy_prices.isEmpty() || sell_prices.isEmpty()) return false;
		int buying_price = buy_prices.get(buy_prices.size()-1);
		int selling_price = sell_prices.get(0);
		if (buying_price >= selling_price) {
			int buying_volume = buy_volumes.get(buy_volumes.size()-1);
			int selling_volume = sell_volumes.get(0);
			int volume = Math.min(buying_volume, selling_volume);
			int new_buying_volume = buying_volume - volume;
			int new_selling_volume = selling_volume - volume;
			if (new_buying_volume == 0) {
				buy_prices.remove(buy_prices.size()-1);
				buy_volumes.remove(buy_volumes.size()-1);
			} else {
				buy_volumes.set(buy_prices.size()-1, new_buying_volume);
			}
			if (new_selling_volume == 0) {
				sell_prices.remove(0);
				sell_volumes.remove(0);
			} else {
				sell_volumes.set(0, new_selling_volume);
			}
			System.out.println("TRADE " + volume + "@" + (buy ? selling_price : buying_price));
			return true;
		} else {
			return false;
		}
	}
	
	private void process_order(int volume, int price, boolean buy) {
		if (volume < 1 || price < 1) {
			System.err.println("Error: Non-positive argument");
		} else {
			int index = find_index(price, buy);
			if (buy) {
				buy_prices.add(index, price);
				buy_volumes.add(index, volume);
			} else {
				sell_prices.add(index, price);
				sell_volumes.add(index, volume);
			}
			// Handle trades
			while(trade_occurs(buy));
		}
	}
	
	public void process_line(String[] args) {
		switch(args[0]) {
		case "BUY":
			try {
				process_order(Integer.parseInt(args[1]), Integer.parseInt(args[2]), true);
			} catch (NumberFormatException e) {
				e.printStackTrace(System.err);
			}
			break;
		case "SELL":
			try {
				process_order(Integer.parseInt(args[1]), Integer.parseInt(args[2]), false);
			} catch (NumberFormatException e) {
				e.printStackTrace(System.err);
			}
			break;
		case "PRINT":
			System.out.println("--- BUY ---");
			for(int i = buy_prices.size()-1; i >= 0; i--) {
				System.out.println("BUY " + buy_volumes.get(i) + "@" + buy_prices.get(i));
			}
			System.out.println("--- SELL ---");
			for(int i = 0; i < sell_prices.size(); i++) {
				System.out.println("SELL " + sell_volumes.get(i) + "@" + sell_prices.get(i));
			}
			break;
		default:
			System.err.println("Error: Unrecognized argument");
		}
	}

}
