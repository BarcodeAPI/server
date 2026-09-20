package org.barcodeapi.server.core;

import java.net.InetAddress;
import java.net.UnknownHostException;

public final class CidrTree<T> {

	private static final class Node<T> {
		Node<T> bit0;
		Node<T> bit1;
		T value;
	}

	private Node<T> root = new Node<>();

	public void put(String entry, T value) {
		int slash = entry.indexOf('/');

		if (slash < 0) {
			entry += "/32";
			slash = entry.length() - 3;
		}

		try {
			
			// Resolve IP to byte array
			byte[] network = InetAddress.getByName(//
					entry.substring(0, slash)).getAddress();

			if (network.length != 4) {
				throw new IllegalArgumentException("IPv4 only: " + entry);
			}

			// Validate group is between 0 and 32
			int group = Integer.parseInt(entry.substring(slash + 1));
			if (group < 0 || group > 32) {
				throw new IllegalArgumentException("Invalid prefix: " + entry);
			}

			Node<T> node = root;

			for (int bit = 0; bit < group; bit++) {
				
				int valueBit = (network[bit >>> 3] >>> (7 - (bit & 7))) & 1;

				if (valueBit == 0) {
					if (node.bit0 == null) {
						node.bit0 = new Node<>();
					}

					node = node.bit0;
				} else {
					if (node.bit1 == null) {
						node.bit1 = new Node<>();
					}

					node = node.bit1;
				}
			}

			node.value = value;

		} catch (UnknownHostException e) {
			throw new IllegalArgumentException("Invalid IPv4 CIDR: " + entry, e);
		}
	}

	public T get(String ip) {
		try {
			
			// Resolve IP to byte array
			byte[] address = InetAddress.getByName(ip).getAddress();

			if (address.length != 4) {
				throw new IllegalArgumentException("IPv4 only: " + ip);
			}

			Node<T> node = root;
			T match = node.value;

			for (int bit = 0; bit < 32; bit++) {
				int valueBit = (address[bit >>> 3] >>> (7 - (bit & 7))) & 1;

				node = valueBit == 0 ? node.bit0 : node.bit1;

				if (node == null) {
					break;
				}

				if (node.value != null) {
					match = node.value;
				}
			}

			return match;

		} catch (UnknownHostException e) {
			throw new IllegalArgumentException("Invalid IPv4 address: " + ip, e);
		}
	}

	public void clear() {
		root = new Node<>();
	}
}
