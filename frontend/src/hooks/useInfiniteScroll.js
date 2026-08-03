import { useEffect, useRef } from "react";

export function useInfiniteScroll(
    loadMore,
    { rootMargin = "200px" } = {}
) {

    const sentinelRef = useRef(null);
    const loadMoreRef = useRef(loadMore);

    // Keep the latest loadMore callback
    useEffect(() => {
        loadMoreRef.current = loadMore;
    }, [loadMore]);

    // Create the observer only once
    useEffect(() => {

        const sentinel = sentinelRef.current;
        if (!sentinel) return;

        const observer = new IntersectionObserver(
            (entries) => {
                if (entries[0].isIntersecting) {
                    loadMoreRef.current();
                }
            },
            {
                rootMargin
            }
        );

        observer.observe(sentinel);

        return () => observer.disconnect();

    }, [rootMargin]);

    return sentinelRef;
}