package org.zkoss.zkspringboot.demo;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.util.Monitor;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;

public class TestPerformanceMeter implements Monitor {

	final Tracer tracer = GlobalOpenTelemetry.getTracer("ZK_Tracer", "1.0");

	@Override
	public void sessionCreated(Session sess) {

	}

	@Override
	public void sessionDestroyed(Session sess) {

	}

	@Override
	public void desktopCreated(Desktop desktop) {

	}

	@Override
	public void desktopDestroyed(Desktop desktop) {

	}

	@Override
	public void beforeUpdate(Desktop desktop, List<AuRequest> requests) {
		final AtomicReference<String> commandRef = new AtomicReference<>();
		requests.forEach(req -> {
			final String cmd = req.getCommand();
			if (cmd.equals("rmDesktop")) {
				return;
			}
			commandRef.set(cmd);
		});
		var span = tracer.spanBuilder(desktop.getRequestPath()).setSpanKind(SpanKind.SERVER)
				.setStartTimestamp(ZonedDateTime.now().toInstant()).startSpan();
		span.makeCurrent();
		if (commandRef.get() != null) {
			span.addEvent(commandRef.get());
		}
		desktop.setAttribute("span", span);
	}

	@Override
	public void afterUpdate(Desktop desktop) {
		if (desktop.removeAttribute("span") instanceof Span span) {
			span.end(ZonedDateTime.now().toInstant());
		}
	}

}
