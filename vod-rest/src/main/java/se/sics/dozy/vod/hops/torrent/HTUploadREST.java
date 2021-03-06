/*
 * Copyright (C) 2016 Swedish Institute of Computer Science (SICS) Copyright (C)
 * 2016 Royal Institute of Technology (KTH)
 *
 * Dozy is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package se.sics.dozy.vod.hops.torrent;

import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.dozy.DozyResource;
import se.sics.dozy.DozyResult;
import se.sics.dozy.DozySyncI;
import se.sics.dozy.vod.DozyVoD;
import se.sics.dozy.vod.hops.torrent.model.HTUploadJSON;
import se.sics.dozy.vod.model.ErrorDescJSON;
import se.sics.dozy.vod.model.SuccessJSON;
import se.sics.dozy.vod.util.ResponseStatusMapper;
import se.sics.ktoolbox.util.identifiable.overlay.OverlayIdFactory;
import se.sics.nstream.hops.library.event.core.HopsTorrentUploadEvent;

/**
 * @author Alex Ormenisan <aaor@kth.se>
 */
public class HTUploadREST implements DozyResource {

    //TODO Alex - make into config?
    public static long timeout = 5000;

    private static final Logger LOG = LoggerFactory.getLogger(DozyResource.class);

    private DozySyncI vodTorrentI = null;
    protected OverlayIdFactory torrentIdFactory;

    public HTUploadREST(OverlayIdFactory torrentIdFactory) {
        this.torrentIdFactory = torrentIdFactory;
    }

    @Override
    public void initialize(Map<String, DozySyncI> interfaces) {
        vodTorrentI = interfaces.get(DozyVoD.hopsTorrentDozyName);
        if (vodTorrentI == null) {
            throw new RuntimeException("no sync interface found for vod REST API");
        }
    }

    protected Response upload(HopsTorrentUploadEvent.Request request) {
        LOG.trace("received upload torrent request:{}", request.torrentId);
        LOG.debug("received upload torrent:{} pid:{} did{}", new Object[]{request.torrentId, request.projectId, request.datasetId});
        if (!vodTorrentI.isReady()) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(new ErrorDescJSON("vod not ready")).build();
        }

        LOG.debug("waiting for upload:{}<{}> response", request.torrentId, request.eventId);
        DozyResult<HopsTorrentUploadEvent.Response> result = vodTorrentI.sendReq(request, timeout);
        Pair<Response.Status, String> wsStatus = ResponseStatusMapper.resolveHopsTorrentUpload(result);
        LOG.info("upload:{}<{}> status:{} details:{}", new Object[]{request.torrentId, request.eventId, wsStatus.getValue0(), wsStatus.getValue1()});
        if (wsStatus.getValue0().equals(Response.Status.OK)) {
            return Response.status(Response.Status.OK).entity(new SuccessJSON()).build();
        } else {
            return Response.status(wsStatus.getValue0()).entity(new ErrorDescJSON(wsStatus.getValue1())).build();
        }
    }

    @Path("/torrent/hops/upload/basic")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    /**
     * consumes HTUploadJSON.Basic produces SuccessJSON
     */
    public static class Basic extends HTUploadREST {

        public Basic(OverlayIdFactory overlayIdFactory) {
            super(overlayIdFactory);
        }

        @POST
        public Response uploadBasic(HTUploadJSON.Basic req) {
            return upload(req.resolve(torrentIdFactory));
        }
    }

    @Path("/torrent/hops/upload/xml")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    /**
     * consumes HTUploadJSON.XML produces SuccessJSON
     */
    public static class XML extends HTUploadREST {

        public XML(OverlayIdFactory overlayIdFactory) {
            super(overlayIdFactory);
        }

        @POST
        public Response uploadXML(HTUploadJSON.XML req) {
            return upload(req.resolve(torrentIdFactory));
        }
    }
}
