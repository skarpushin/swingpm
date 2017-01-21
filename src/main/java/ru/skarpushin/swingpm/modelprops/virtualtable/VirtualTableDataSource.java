package ru.skarpushin.swingpm.modelprops.virtualtable;

import org.summerb.approaches.jdbccrud.api.dto.PagerParams;
import org.summerb.approaches.jdbccrud.api.dto.PaginatedList;

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
