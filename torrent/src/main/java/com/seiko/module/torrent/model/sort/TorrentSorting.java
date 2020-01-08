/*
 * Copyright (C) 2016, 2017 Yaroslav Pronin <proninyaroslav@mail.ru>
 *
 * This file is part of LibreTorrent.
 *
 * LibreTorrent is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LibreTorrent is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LibreTorrent.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.seiko.module.torrent.model.sort;


import com.seiko.module.torrent.model.TorrentListItem;

public class TorrentSorting extends BaseSorting
{
    public enum SortingColumns implements SortingColumnsInterface<TorrentListItem>
    {
        none {
            @Override
            public int compare(TorrentListItem item1, TorrentListItem item2,
                               Direction direction)
            {
                return 0;
            }
        },
        name {
            @Override
            public int compare(TorrentListItem item1, TorrentListItem item2,
                               Direction direction)
            {
                if (direction == Direction.ASC)
                    return item1.getName().compareTo(item2.getName());
                else
                    return item2.getName().compareTo(item1.getName());
            }
        },
        size {
            @Override
            public int compare(TorrentListItem item1, TorrentListItem item2,
                               Direction direction)
            {
                if (direction == Direction.ASC)
                    return Long.compare(item2.getTotalBytes(), item1.getTotalBytes());
                else
                    return Long.compare(item1.getTotalBytes(), item2.getTotalBytes());
            }
        },
        progress {
            @Override
            public int compare(TorrentListItem item1, TorrentListItem item2,
                               Direction direction)
            {
                if (direction == Direction.ASC)
                    return Integer.compare(item2.getProgress(), item1.getProgress());
                else
                    return Integer.compare(item1.getProgress(), item2.getProgress());
            }
        },
        ETA {
            @Override
            public int compare(TorrentListItem item1, TorrentListItem item2,
                               Direction direction)
            {
                if (direction == Direction.ASC)
                    return Long.compare(item2.getETA(), item1.getETA());
                else
                    return Long.compare(item1.getETA(), item2.getETA());
            }
        },
        peers {
            @Override
            public int compare(TorrentListItem item1, TorrentListItem item2,
                               Direction direction)
            {
                if (direction == Direction.ASC)
                    return Integer.compare(item2.getPeers(), item1.getPeers());
                else
                    return Integer.compare(item1.getPeers(), item2.getPeers());
            }
        },
        dateAdded {
            @Override
            public int compare(TorrentListItem item1, TorrentListItem item2,
                               Direction direction)
            {
                if (direction == Direction.ASC)
                    return Long.compare(item2.getDateAdded(), item1.getDateAdded());
                else
                    return Long.compare(item1.getDateAdded(), item2.getDateAdded());
            }
        };

        public static String[] valuesToStringArray()
        {
            SortingColumns[] values = SortingColumns.class.getEnumConstants();
            String[] arr = new String[values.length];

            for (int i = 0; i < values.length; i++)
                arr[i] = values[i].toString();

            return arr;
        }

        public static SortingColumns fromValue(String value)
        {
            for (SortingColumns column : SortingColumns.class.getEnumConstants())
                if (column.toString().equalsIgnoreCase(value))
                    return column;

            return SortingColumns.none;
        }
    }

    public TorrentSorting(SortingColumns columnName, Direction direction)
    {
        super(columnName.name(), direction);
    }

    public TorrentSorting()
    {
        this(SortingColumns.name, Direction.DESC);
    }
}
