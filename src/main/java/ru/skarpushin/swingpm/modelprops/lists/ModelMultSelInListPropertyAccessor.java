package ru.skarpushin.swingpm.modelprops.lists;

import ru.skarpushin.swingpm.base.HasValidationErrorsListEx;
import ru.skarpushin.swingpm.collections.HasListExEvents;

public interface ModelMultSelInListPropertyAccessor<E> extends HasListExEvents<E>, HasValidationErrorsListEx {
	/**
	 * Return all optionsAccessor we can choose from. Normally used to populate
	 * list
	 * 
	 * @return
	 */
	ModelListPropertyAccessor<E> getOptions();

	/**
	 * Return managed list accessor which holds selection stateId
	 * 
	 * @return
	 */
	ModelListPropertyAccessor<E> getSelectionAccessor();

	void setNewSelection(Object[] selectedObjects);
}
