/*
 * File name: ClientObservable.java
 * 
 * Purpose: 
 * 
 * © Copyright Intel Corporation. All rights reserved.
 * Intel Corporation, 2200 Mission College Boulevard,
 * Santa Clara, CA 95052-8119, USA
 */
package com.intel.jndn.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import net.named_data.jndn.OnData;
import net.named_data.jndn.OnInterest;
import net.named_data.jndn.OnTimeout;
import net.named_data.jndn.transport.Transport;

/**
 *
 * @author Andrew Brown <andrew.brown@intel.com>
 */
public class ClientObservable extends Observable implements OnData, OnTimeout, OnInterest {

  protected List<ClientObservableEvent> events = new ArrayList<>();
  protected List<Interest> incomingInterestPackets = new ArrayList<>();
  protected List<Data> incomingDataPackets;

  /**
   * Generic notification
   * 
   * @param <T>
   * @param packet
   */
  public <T> void notify(T packet) {
    setChanged();
    notifyObservers(new ClientObservableEvent(packet));
  }

  /**
   * Handle data packets
   * 
   * @param interest
   * @param data 
   */
  @Override
  public void onData(Interest interest, Data data) {
    notify(data);
  }

  /**
   * Handle exceptions
   * 
   * @param e 
   */
  public void onError(Exception e) {
    notify(e);
  }

  /**
   * Handle timeouts
   * 
   * @param interest 
   */
  @Override
  public void onTimeout(Interest interest) {
    notify(interest);
  }

  /**
   * Handle incoming interests
   * 
   * @param prefix
   * @param interest
   * @param transport
   * @param registeredPrefixId 
   */
  @Override
  public void onInterest(Name prefix, Interest interest, Transport transport, long registeredPrefixId) {
    notify(interest); // TODO wrap
  }

  /**
   * Helper to reference both outgoing interest and incoming data packets
   */
  class InterestDataPacket {

    private Interest interest;
    private Data data;

    public InterestDataPacket(Interest interest, Data data) {
      this.interest = interest;
      this.data = data;
    }

    public Data getData() {
      return data;
    }

    public Interest getInterest() {
      return interest;
    }
  }
  
  /**
   * Helper to reference both incoming interest and the transport to send data on
   */
  class InterestTransportPacket{
    private Interest interest;
    private Transport transport;

    public InterestTransportPacket(Interest interest, Transport transport) {
      this.interest = interest;
      this.transport = transport;
    }

    public Interest getInterest() {
      return interest;
    }

    public Transport getTransport() {
      return transport;
    }
    
  }
}
