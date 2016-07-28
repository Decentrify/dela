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
package se.sics.dozy.vod.hops.torrent.model;

import java.util.ArrayList;
import java.util.List;
import org.javatuples.Pair;
import se.sics.dozy.vod.model.AddressJSON;
import se.sics.dozy.vod.model.TorrentIdJSON;
import se.sics.dozy.vod.model.hops.util.HDFSEndpointJSON;
import se.sics.dozy.vod.model.hops.util.HDFSResourceJSON;
import se.sics.ktoolbox.util.identifiable.Identifier;
import se.sics.ktoolbox.util.network.KAddress;
import se.sics.nstream.hops.hdfs.HDFSEndpoint;
import se.sics.nstream.hops.hdfs.HDFSResource;
import se.sics.nstream.hops.library.event.core.HopsTorrentDownloadEvent;

/**
 * @author Alex Ormenisan <aaor@kth.se>
 */
public class HTStartDownloadJSON {
    public static abstract class Base {
        protected TorrentIdJSON torrentId;
        protected HDFSResourceJSON manifestHDFSResource;
        protected List<AddressJSON> partners;
        
        public TorrentIdJSON getTorrentId() {
            return torrentId;
        }

        public void setTorrentId(TorrentIdJSON torrentId) {
            this.torrentId = torrentId;
        }
        
        public HDFSResourceJSON getManifestHDFSResource() {
            return manifestHDFSResource;
        }

        public void setManifestHDFSResource(HDFSResourceJSON manifestHDFSResource) {
            this.manifestHDFSResource = manifestHDFSResource;
        }

        public List<AddressJSON> getPartners() {
            return partners;
        }

        public void setPartners(List<AddressJSON> partners) {
            this.partners = partners;
        }
        
        protected HopsTorrentDownloadEvent.StartRequest partialResolve(HDFSEndpoint mhe) {
            Identifier tId = torrentId.resolve();
            HDFSResource mhr = manifestHDFSResource.resolve();
            List<KAddress> p = new ArrayList<>();
            for (AddressJSON partner : partners) {
                p.add(partner.resolve());
            }
            return new HopsTorrentDownloadEvent.StartRequest(tId, Pair.with(mhe, mhr), p);
        }
    }
    
    public static class Basic extends Base {
        private HDFSEndpointJSON.Basic manifestHDFSEndpoint;

        public HDFSEndpointJSON.Basic getManifestHDFSEndpoint() {
            return manifestHDFSEndpoint;
        }

        public void setManifestHDFSEndpoint(HDFSEndpointJSON.Basic manifestHDFSEndpoint) {
            this.manifestHDFSEndpoint = manifestHDFSEndpoint;
        }

        public HopsTorrentDownloadEvent.StartRequest resolve() {
            HDFSEndpoint mhe = manifestHDFSEndpoint.resolve();
            return partialResolve(mhe);
        }
    }

    public static class XML extends Base {

        private HDFSEndpointJSON.XML manifestHDFSEndpoint;

        public HDFSEndpointJSON.XML getManifestHDFSEndpoint() {
            return manifestHDFSEndpoint;
        }

        public void setManifestHDFSEndpoint(HDFSEndpointJSON.XML manifestHDFSEndpoint) {
            this.manifestHDFSEndpoint = manifestHDFSEndpoint;
        }

        public HopsTorrentDownloadEvent.StartRequest resolve() {
            HDFSEndpoint mhe = manifestHDFSEndpoint.resolve();
            return partialResolve(mhe);
        }
    }
}
