package io.eventuate.coordination.leadership;

public interface LeaderSelectedCallback {
  void run(LeadershipController leadershipController);
}
