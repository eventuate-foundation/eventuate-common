package io.eventuate.common.id;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationIdGenerator implements IdGenerator {

  private Logger logger = LoggerFactory.getLogger(getClass());
  private static final long MAX_COUNTER = 1 << 16;

  private Long macAddress;
  private long currentPeriod = timeNow();
  private long counter = 0;


  public ApplicationIdGenerator() {
    try {
      macAddress = getMacAddress();
      logger.debug("Mac address {}", macAddress);
      if (macAddress == null)
        throw new RuntimeException("Cannot find mac address");
    } catch (SocketException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean databaseIdRequired() {
    return false;
  }

  private Long getMacAddress() throws SocketException {
    String ma = System.getenv("EVENTUATE_MAC_ADDRESS");
    if (ma != null)
        return Long.parseLong(ma);
    Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
    while (ifaces.hasMoreElements()) {
      NetworkInterface iface = ifaces.nextElement();
      if (iface.getHardwareAddress() != null) {
        return toLong(iface.getHardwareAddress());
      }
    }
    return null;
  }

  private Long toLong(byte[] bytes) {
    long result = 0L;
    for (byte b : bytes) {
      result = (result << 8) + (b & 0xff);
    }
    return result;
  }

  private long timeNow() {
    return System.currentTimeMillis();
  }

  private Int128 makeId() {
    return new Int128(currentPeriod, (macAddress << 16) + counter);
  }

  public Int128 genIdInternal() {
    long now = timeNow();
    if (currentPeriod != now || counter == MAX_COUNTER) {
      long oldPeriod = this.currentPeriod;
      while ((this.currentPeriod = timeNow()) <= oldPeriod) {
        // Just do nothing
      }
      counter = 0;
    }
    Int128 id = makeId();
    counter = counter + 1;
    return id;
  }

  @Override
  public synchronized Int128 genId(Long databaseId, Integer partitionOffset) {
    return genIdInternal();
  }

  @Override
  public Optional<Int128> incrementIdIfPossible(Int128 anchorId) {
    return Optional.of(genId(null, null));
  }
}
