module net.pincette.operator.testutil {
  requires kubernetes.client.api;
  requires kubernetes.model.core;
  requires net.pincette.common;

  exports net.pincette.operator.testutil;
}
