package ch.swiftapp.erp.qualitycontrol.dto;
import ch.swiftapp.erp.qualitycontrol.model.NcrSeverity;
import jakarta.validation.constraints.*; import java.util.UUID;
public record NcrRequest(@NotNull UUID qualityCheckId, @NotNull NcrSeverity severity, @NotBlank String description, String correctiveAction) {}

