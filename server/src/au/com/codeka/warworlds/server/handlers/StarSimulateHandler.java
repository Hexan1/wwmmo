package au.com.codeka.warworlds.server.handlers;

import java.io.IOException;
import java.io.PrintWriter;

import au.com.codeka.common.model.Simulation;
import au.com.codeka.warworlds.server.RequestException;
import au.com.codeka.warworlds.server.RequestHandler;
import au.com.codeka.warworlds.server.ctrl.StarController;
import au.com.codeka.warworlds.server.model.Star;

public class StarSimulateHandler extends RequestHandler {

    @Override
    protected void get() throws RequestException {
        boolean dolog = true;
        if (getRequest().getParameter("dolog") != null) {
            dolog = getRequest().getParameter("dolog").equals("1");
        }
        simulate(Integer.parseInt(getUrlParameter("starid")), false, dolog);
    }

    @Override
    protected void post() throws RequestException {
        boolean update = false;
        boolean dolog = true;
        if (getRequest().getParameter("update") != null) {
            update = getRequest().getParameter("update").equals("1");
        }
        if (getRequest().getParameter("dolog") != null) {
            dolog = getRequest().getParameter("dolog").equals("1");
        }
        simulate(Integer.parseInt(getUrlParameter("starid")), update, dolog);
    }

    private void simulate(int starID, boolean update, boolean dolog) throws RequestException {
        PrintWriter printWriter = null;
        if (dolog) {
            try {
                printWriter = getResponse().getWriter();
            } catch (IOException e) {
                throw new RequestException(e);
            }
        }
        final PrintWriter outw = printWriter;

        Simulation sim = new Simulation(new Simulation.LogHandler() {
            @Override
            public void setStarName(String starName) {}

            @Override
            public void log(String message) {
                if (outw != null) {
                    outw.println(message);
                }
            }
        });

        Star star = new StarController().getStar(starID);
        sim.simulate(star);

        if (update) {
            new StarController().update(star);
        }
    }
}
