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
package se.sics.dozy.vod.system.config.printer;

import java.util.HashMap;
import java.util.Map;
import se.sics.gvod.stream.torrent.TorrentComp;
import se.sics.ktoolbox.croupier.CroupierComp;
import se.sics.ktoolbox.util.profiling.KProfiler;
import se.sics.ktoolbox.util.profiling.KProfilerRegistry;
import se.sics.ktoolbox.util.profiling.KProfilerRegistryConverter;

/**
 * @author Alex Ormenisan <aaor@kth.se>
 */
public class KProfilerPrinter {
     public static void main(String[] args) {
         Map<String, KProfiler.Type> kProfilerRegistryMap = new HashMap<>();
         kProfilerRegistryMap.put(TorrentComp.class.getCanonicalName(), KProfiler.Type.LOG);
         KProfilerRegistry kProfilerRegistry = new KProfilerRegistry(kProfilerRegistryMap);
         System.out.println(KProfilerRegistryConverter.jsonPrettyPrint(kProfilerRegistry));
     }
             
}
