package net.pincette.operator.testutil;

import static java.time.Duration.ofMillis;
import static net.pincette.util.Util.doUntil;
import static net.pincette.util.Util.tryToDoSilent;
import static net.pincette.util.Util.tryToGet;

import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.Resource;
import java.io.InputStream;
import java.time.Duration;

/**
 * Simple utilities for unit tests with the Java Operator SDK.
 *
 * @author Werner Donné
 */
public class Util {
  private static final Duration WAIT_INTERVAL = ofMillis(100);

  private Util() {}

  public static void createNamespace(final KubernetesClient client, final String name) {
    createOrReplaceAndWait(
        client
            .namespaces()
            .resource(
                new NamespaceBuilder()
                    .withApiVersion("v1")
                    .withNewMetadata()
                    .withName(name)
                    .endMetadata()
                    .build()));
  }

  public static <T> void createOrReplaceAndWait(final Resource<T> resource) {
    resource.serverSideApply();
    waitForCreate(resource);
  }

  public static <T> void deleteAndWait(final Resource<T> resource) {
    resource.delete();
    waitForDelete(resource);
  }

  public static void deleteCustomResource(final KubernetesClient client, final String name) {
    deleteAndWait(client.apiextensions().v1().customResourceDefinitions().withName(name));
  }

  public static void deleteNamespace(final KubernetesClient client, final String name) {
    deleteAndWait(client.namespaces().withName(name));
  }

  public static void loadCustomResource(final KubernetesClient client, final InputStream in) {
    tryToDoSilent(() -> client.apiextensions().v1().customResourceDefinitions().load(in).create());
  }

  public static <T> void waitForCreate(final Resource<T> resource) {
    doUntil(() -> tryToGet(() -> resource.get() != null, e -> false).orElse(false), WAIT_INTERVAL);
  }

  public static <T> void waitForDelete(final Resource<T> resource) {
    doUntil(() -> tryToGet(() -> resource.get() == null, e -> true).orElse(true), WAIT_INTERVAL);
  }
}
