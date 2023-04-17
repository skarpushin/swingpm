/*******************************************************************************
 * Copyright 2015-2023 Sergey Karpushin
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package ru.skarpushin.swingpm.modelprops.virtualtable;

import org.summerb.easycrud.api.dto.PagerParams;
import org.summerb.easycrud.api.dto.PaginatedList;

import ru.skarpushin.swingpm.collections.FilterPredicate;

/**
 * Adapter for loading additional “pages” of data on demand. Provided to
 * AsyncDataLoader.
 * 
 * Note that impl must respect other data filtering criteria specific to usage
 * situation.
 * 
 * Note base interface FilterPredicate - impl must be able to check if data
 * satisfy context filtering parameters.
 * 
 * @see AsyncDataLoader
 */
public interface VirtualTableDataSource<E> extends FilterPredicate<E> {
	PaginatedList<E> loadData(PagerParams pagerParams);
}
