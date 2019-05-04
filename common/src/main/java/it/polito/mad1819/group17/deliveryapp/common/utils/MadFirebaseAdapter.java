package it.polito.mad1819.group17.deliveryapp.common.utils;

import android.arch.lifecycle.LifecycleObserver;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;

import com.firebase.ui.database.ChangeEventListener;
import com.firebase.ui.database.FirebaseArray;
import com.firebase.ui.database.ObservableSnapshotArray;
import com.google.firebase.database.DatabaseReference;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
interface MadFirebaseAdapter<T> extends ChangeEventListener, LifecycleObserver {
    /**
     * If you need to do some setup before the adapter starts listening for change events in the
     * database, do so it here and then call {@code super.startListening()}.
     */
    void startListening();

    /**
     * Removes listeners and clears all items in the backing {@link FirebaseArray}.
     */
    void stopListening();

    /**
     * Returns the backing {@link ObservableSnapshotArray} used to populate this adapter.
     *
     * @return the backing snapshot array
     */
    @NonNull
    ObservableSnapshotArray<T> getSnapshots();

    /**
     * Gets the item at the specified position from the backing snapshot array.
     *
     * @see ObservableSnapshotArray#get(int)
     */
    @NonNull
    T getItem(int position);
}