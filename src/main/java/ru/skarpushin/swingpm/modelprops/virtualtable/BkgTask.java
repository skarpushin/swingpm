/*******************************************************************************
 * Copyright 2015-2021 Sergey Karpushin
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

public abstract class BkgTask<E> {
	public static final int STATUS_PENDING = 0;
	public static final int STATUS_PERFORMED = 2;

	protected AsyncDataLoaderImpl<E> loader;
	protected Object stateId;
	protected int status = STATUS_PENDING;

	protected volatile BkgTask<E> next;

	public BkgTask(AsyncDataLoaderImpl<E> loader, Object stateId) {
		this.loader = loader;
		this.stateId = stateId;
	}

	public boolean isPerformed() {
		return status == STATUS_PERFORMED;
	}

	public abstract void perform();

	public BkgTask<E> findTail() {
		BkgTask<E> ret = this;
		while (ret.next != null) {
			ret = ret.next;
		}
		return ret;
	}

	public BkgTask<E> findNextForProcessing() {
		BkgTask<E> cur = this;
		while (cur != null) {
			if (cur.stateId != loader.currentStateId) {
				// NOTE: Yes, reference equality used here. Yes, by intention
				// (for faster comparison).
				cur = cur.next;
				continue;
			}

			if (cur.status == STATUS_PENDING) {
				return cur;
			}

			cur = cur.next;
		}
		return null;
	}
}
