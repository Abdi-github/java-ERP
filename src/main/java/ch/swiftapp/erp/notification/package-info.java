/**
 * Notification module — manages in-app notifications, transactional emails,
 * scheduled digest emails, and mass-mail campaigns.
 *
 * <p>This module is a <em>pure consumer</em> of domain events from all other modules.
 * It never imports internal classes from other modules — only their public events
 * (in the module root or {@code event/} package) and public API interfaces.</p>
 *
 * <h2>Public API</h2>
 * <ul>
 *   <li>{@link ch.swiftapp.erp.notification.NotificationModuleApi} — query unread count,
 *       mark notifications read, trigger ad-hoc notifications</li>
 * </ul>
 *
 * <h2>Communication</h2>
 * <ul>
 *   <li>Inbound: Spring Application Events from sales, purchasing, production,
 *       quality-control, HR, and auth modules</li>
 *   <li>Outbound: none — purely reactive</li>
 * </ul>
 */
package ch.swiftapp.erp.notification;

