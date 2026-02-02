package ch.swiftapp.erp.notification.model;

/**
 * Lifecycle status of a {@link MailCampaign}.
 */
public enum MailCampaignStatus {
    /** Campaign created but not yet queued for sending. */
    DRAFT,
    /** Approved and waiting for the batch scheduler to pick it up. */
    QUEUED,
    /** Currently being processed by the batch mail sender. */
    RUNNING,
    /** All recipients processed successfully. */
    COMPLETED,
    /** Processing stopped due to an unrecoverable error. */
    FAILED,
    /** Manually cancelled by an admin before completion. */
    CANCELLED
}

