/*
 * Copyright 2000-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.crystal.intellij.parser.builder;

import com.intellij.util.containers.IntStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

/**
 * @author peter
 */
final class MarkerPool extends ObjectArrayList<LazyPsiBuilder.ProductionMarker> {
  private final LazyPsiBuilder myBuilder;
  private final IntStack myFreeStartMarkers = new IntStack();
  private final IntStack myFreeErrorItems = new IntStack();

  MarkerPool(LazyPsiBuilder builder) {
    myBuilder = builder;
    add(null); //no marker has id 0
  }

  LazyPsiBuilder.StartMarker allocateStartMarker() {
    if (myFreeStartMarkers.size() > 0) {
      return (LazyPsiBuilder.StartMarker)get(myFreeStartMarkers.pop());
    }

    LazyPsiBuilder.StartMarker marker = new LazyPsiBuilder.StartMarker(size(), myBuilder);
    add(marker);
    return marker;
  }

  LazyPsiBuilder.ErrorItem allocateErrorItem() {
    if (myFreeErrorItems.size() > 0) {
      return (LazyPsiBuilder.ErrorItem)get(myFreeErrorItems.pop());
    }
    
    LazyPsiBuilder.ErrorItem item = new LazyPsiBuilder.ErrorItem(size(), myBuilder);
    add(item);
    return item;
  }

  void freeMarker(LazyPsiBuilder.ProductionMarker marker) {
    marker.clean();
    (marker instanceof LazyPsiBuilder.StartMarker ? myFreeStartMarkers : myFreeErrorItems).push(marker.markerId);
  }

}
